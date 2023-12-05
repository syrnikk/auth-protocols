package com.syrnik.authprotocolsbackend.security.jwt;

import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.AUTHORITIES;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.EMAIL;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.FIRST_NAME;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.LAST_NAME;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.syrnik.authprotocolsbackend.security.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String ROLE_PREFIX = "ROLE_";

    @Value("${app.jwt.access-token.secret}")
    private String jwtAccessTokenSecret;

    @Value("${app.jwt.access-token.validity}")
    private Long jwtAccessTokenValidityInMinutes;

    @Value("${app.jwt.refresh-token.secret}")
    private String jwtRefreshTokenSecret;

    @Value("${app.jwt.refresh-token.validity}")
    private Long jwtRefreshTokenValidityInMinutes;

    public String generateAccessToken(Claims claims) {
        return generateToken(claims, jwtAccessTokenSecret, jwtAccessTokenValidityInMinutes);
    }

    public String generateRefreshToken(Claims claims) {
        return generateToken(claims, jwtRefreshTokenSecret, jwtRefreshTokenValidityInMinutes);
    }

    private String generateToken(Claims claims, String tokenSecret, Long tokenValidityInMinutes) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidityInMinutes * 60 * 1000);

        return Jwts
              .builder()
              .issuedAt(now)
              .expiration(expiration)
              .claims(claims)
              .signWith(SignatureAlgorithm.HS512, tokenSecret)
              .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = extractAccessTokenClaims(accessToken);

        String email = (String) claims.get(EMAIL);
        String firstName = (String) claims.get(FIRST_NAME);
        String lastName = (String) claims.get(LAST_NAME);
        List<String> authorities = (List<String>) claims.get(AUTHORITIES);
        Set<SimpleGrantedAuthority> grantedAuthorities = authorities
              .stream()
              .map(authority -> authority.startsWith(ROLE_PREFIX) ? new SimpleGrantedAuthority(authority) :
                                new SimpleGrantedAuthority(ROLE_PREFIX + authority.toUpperCase()))
              .collect(Collectors.toUnmodifiableSet());

        CustomUserDetails userDetails = new CustomUserDetails(claims.getSubject(), "", email, firstName, lastName,
              grantedAuthorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessTokenSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshTokenSecret);
    }

    private boolean validateToken(String token, String tokenSecret) {
        try {
            Jwts.parser().setSigningKey(tokenSecret).build().parseSignedClaims(token);
            return true;
        } catch(SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch(MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch(ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch(UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch(IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public Claims extractAccessTokenClaims(String accessToken) {
        return extractAllClaims(accessToken, jwtAccessTokenSecret);
    }

    public Claims extractRefreshTokenClaims(String refreshToken) {
        return extractAllClaims(refreshToken, jwtRefreshTokenSecret);
    }

    private Claims extractAllClaims(String token, String tokenSecret) {
        return Jwts.parser().setSigningKey(tokenSecret).build().parseSignedClaims(token).getPayload();
    }
}

package com.syrnik.authprotocolsbackend.security.jwt;

import static com.syrnik.authprotocolsbackend.constant.JwtClaims.AUTHORITIES;
import static com.syrnik.authprotocolsbackend.constant.JwtClaims.PROTOCOL;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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

    @Value("${jwt.access-token.secret}")
    private String jwtAccessTokenSecret;

    @Value("${jwt.access-token.validity}")
    private Long jwtAccessTokenValidityInMinutes;

    @Value("${jwt.refresh-token.secret}")
    private String jwtRefreshTokenSecret;

    @Value("${jwt.refresh-token.validity}")
    private Long jwtRefreshTokenValidityInMinutes;

    public String generateAccessToken(JwtTokenClaims jwtTokenClaims) {
        return generateToken(jwtTokenClaims, jwtAccessTokenSecret, jwtAccessTokenValidityInMinutes);

    }

    public String generateRefreshToken(JwtTokenClaims jwtTokenClaims) {
        return generateToken(jwtTokenClaims, jwtRefreshTokenSecret, jwtRefreshTokenValidityInMinutes);
    }

    private String generateToken(JwtTokenClaims jwtTokenClaims, String tokenSecret, Long tokenValidityInMinutes) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidityInMinutes * 60 * 1000);

        return Jwts
              .builder()
              .subject(jwtTokenClaims.username())
              .issuedAt(now)
              .expiration(expiration)
              .claim(AUTHORITIES, jwtTokenClaims.authorities())
              .claim(PROTOCOL, jwtTokenClaims.protocol())
              .signWith(SignatureAlgorithm.HS512, tokenSecret)
              .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = extractAccessTokenClaims(accessToken);
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", null);
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
package com.syrnik.authprotocolsbackend.security.ldap;

import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.AUTHORITIES;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.PROTOCOL;

import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.enums.AuthProtocol;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LdapAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
          Authentication authentication) throws IOException {
        String accessToken = null;
        String refreshToken = null;
        if(authentication.getPrincipal() instanceof LdapUserDetails ldapUserDetails) {
            Claims claims = Jwts
                  .claims()
                  .subject(ldapUserDetails.getUsername())
                  .add(AUTHORITIES, authentication
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                  .add(PROTOCOL, AuthProtocol.LDAP)
                  .build();
            accessToken = jwtTokenProvider.generateAccessToken(claims);
            refreshToken = jwtTokenProvider.generateRefreshToken(claims);
        }
        objectMapper.writeValue(response.getWriter(), new JwtResponse(accessToken, refreshToken));
    }

}

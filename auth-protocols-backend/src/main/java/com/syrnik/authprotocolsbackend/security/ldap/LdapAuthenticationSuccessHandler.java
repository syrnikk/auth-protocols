package com.syrnik.authprotocolsbackend.security.ldap;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenProvider;

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
            accessToken = jwtTokenProvider.generateAccessToken(ldapUserDetails.getUsername(),
                  authentication.getAuthorities());
            refreshToken = jwtTokenProvider.generateRefreshToken(ldapUserDetails.getUsername(),
                  authentication.getAuthorities());
        }
        objectMapper.writeValue(response.getWriter(), new JwtResponse(accessToken, refreshToken));
    }

}

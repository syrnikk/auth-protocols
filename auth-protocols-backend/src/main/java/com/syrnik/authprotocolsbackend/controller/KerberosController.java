package com.syrnik.authprotocolsbackend.controller;

import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.AUTHORITIES;

import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.kerberos.authentication.KerberosServiceRequestToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kerberos")
public class KerberosController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> kerberosAuthentication(Authentication authentication) {
        if(authentication instanceof KerberosServiceRequestToken token) {
            if(token.getPrincipal() instanceof User user) {
                token.getPrincipal();
                Claims claims = Jwts
                      .claims()
                      .subject(user.getUsername())
                      .add(AUTHORITIES, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                      .build();
                String accessToken = jwtTokenProvider.generateAccessToken(claims);
                String refreshToken = jwtTokenProvider.generateRefreshToken(claims);
                return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
            }
        }
        return ResponseEntity.internalServerError().build();
    }
}

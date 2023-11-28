package com.syrnik.authprotocolsbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.dto.RefreshTokenRequest;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        if(jwtTokenProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtTokenProvider.extractRefreshTokenClaims(refreshToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(claims);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(claims);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

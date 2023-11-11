package com.syrnik.authprotocolsbackend.controller;

import static com.syrnik.authprotocolsbackend.constant.JwtClaims.AUTHORITIES;
import static com.syrnik.authprotocolsbackend.constant.JwtClaims.PROTOCOL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrnik.authprotocolsbackend.dto.AuthDto;
import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.dto.RefreshTokenRequest;
import com.syrnik.authprotocolsbackend.enums.AuthProtocol;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenClaims;
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

    @GetMapping("/auth/me")
    public ResponseEntity<AuthDto> getAuthInfo(Authentication authentication) {
        AuthDto authDto = new AuthDto(AuthProtocol.from(authentication));
        return ResponseEntity.ok(authDto);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        if(jwtTokenProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtTokenProvider.extractRefreshTokenClaims(refreshToken);
            JwtTokenClaims jwtTokenClaims = new JwtTokenClaims(claims.getSubject(), claims.get(AUTHORITIES),
                  claims.get(PROTOCOL));
            String newAccessToken = jwtTokenProvider.generateAccessToken(jwtTokenClaims);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(jwtTokenClaims);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

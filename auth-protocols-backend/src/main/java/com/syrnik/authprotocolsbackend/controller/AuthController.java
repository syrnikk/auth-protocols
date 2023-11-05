package com.syrnik.authprotocolsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrnik.authprotocolsbackend.dto.AuthDto;
import com.syrnik.authprotocolsbackend.enums.AuthProtocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {
    @GetMapping("/auth/me")
    public ResponseEntity<AuthDto> getAuthInfo(Authentication authentication) {
        AuthDto authDto = new AuthDto(AuthProtocol.from(authentication));
        return ResponseEntity.ok(authDto);
    }
}

package com.syrnik.authprotocolsbackend.dto;

public record JwtResponse(String accessToken, String refreshToken) {
}

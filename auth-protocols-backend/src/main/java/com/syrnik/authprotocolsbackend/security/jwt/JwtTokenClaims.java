package com.syrnik.authprotocolsbackend.security.jwt;

public record JwtTokenClaims(String username, Object authorities, Object protocol) {
}

package com.syrnik.authprotocolsbackend.dto;

public record Saml2RequestResponse(String samlIdpService, String authnRequestEncoded) {
}

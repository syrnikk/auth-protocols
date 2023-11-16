package com.syrnik.authprotocolsbackend.controller;

import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.AUTHORITIES;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.PROTOCOL;
import static com.syrnik.authprotocolsbackend.security.jwt.JwtClaims.SESSION_INDEX;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.syrnik.authprotocolsbackend.dto.AssertionData;
import com.syrnik.authprotocolsbackend.dto.JwtResponse;
import com.syrnik.authprotocolsbackend.dto.Saml2RequestResponse;
import com.syrnik.authprotocolsbackend.dto.SamlArtRequest;
import com.syrnik.authprotocolsbackend.enums.AuthProtocol;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenProvider;
import com.syrnik.authprotocolsbackend.security.saml2.Saml2Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class Saml2Controller {

    @Value("${app.saml2.destination}")
    private String destination;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final Saml2Service saml2Service;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/saml2/create")
    public Saml2RequestResponse createSaml2Request() throws Exception {
        String authnRequestEncoded = saml2Service.createAuthnRequestEncoded();
        return new Saml2RequestResponse(destination, authnRequestEncoded);
    }

    @PostMapping("/saml2/acs")
    public ResponseEntity<?> saml2Acs(@RequestParam("SAMLart") String samlArt) {
        String redirectUrl = frontendUrl + "/saml2?samlArt=" + samlArt;
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
    }

    @PostMapping("/saml2/authenticate")
    public ResponseEntity<JwtResponse> saml2Authenticate(@RequestBody SamlArtRequest samlArtRequest) throws Exception {
        AssertionData assertionData = saml2Service.getAssertionData(samlArtRequest.samlArt());
        Claims claims = Jwts
              .claims()
              .subject(assertionData.nameID())
              .add(AUTHORITIES, assertionData.authorities())
              .add(PROTOCOL, AuthProtocol.SAML2)
              .add(SESSION_INDEX, assertionData.sessionIndex())
              .build();
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }
}

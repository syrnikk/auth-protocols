package com.syrnik.authprotocolsbackend.controller;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public/example")
    public ResponseEntity<?> publicExample(Principal principal, Authentication authentication) {
        String name = principal.getName();
        return ResponseEntity.ok(name);
    }

    @GetMapping("/private/example")
    public ResponseEntity<?> privateExample() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/kerberos")
    public ResponseEntity<?> kerberos() {
        return ResponseEntity.ok("OK");
    }
}

package com.syrnik.authprotocolsbackend.security.saml2;

import org.opensaml.core.config.InitializationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Saml2Initializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        InitializationService.initialize();
    }
}

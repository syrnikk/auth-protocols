package com.syrnik.authprotocolsbackend.enums;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

public enum AuthProtocol {
    OIDC,
    SAML2,
    LDAP,
    KERBEROS;

    public static AuthProtocol from(Authentication authentication) {
        if(authentication instanceof Saml2Authentication)
            return SAML2;
        return null;
    }
}

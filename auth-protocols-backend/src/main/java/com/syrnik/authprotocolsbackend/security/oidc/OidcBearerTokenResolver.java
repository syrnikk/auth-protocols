package com.syrnik.authprotocolsbackend.security.oidc;

import static com.syrnik.authprotocolsbackend.enums.RequestAttribute.SKIP_OIDC_FILTER;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class OidcBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        Boolean skipOidcFilter = (Boolean) request.getAttribute(SKIP_OIDC_FILTER.name());
        if(Boolean.TRUE.equals(skipOidcFilter)) {
            return null;
        }
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        return defaultBearerTokenResolver.resolve(request);
    }
}

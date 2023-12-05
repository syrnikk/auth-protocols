package com.syrnik.authprotocolsbackend.security.oidc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import com.syrnik.authprotocolsbackend.security.jwt.JwtClaims;

public class OidcJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    public OidcJwtConverter() {
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> grantedAuthorities = Stream
              .concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), getRoles(jwt).stream())
              .collect(Collectors.toSet());
        String principleName = getPrincipleName(jwt);
        return new JwtAuthenticationToken(jwt, grantedAuthorities, principleName);
    }

    private Collection<GrantedAuthority> getRoles(Jwt jwt) {
        String clientId = jwt.getClaim(JwtClaims.AZP);
        Map<String, Map<String, List<String>>> resourceAccess = jwt.getClaim(JwtClaims.RESOURCE_ACCESS);
        if(resourceAccess == null) {
            return Collections.emptyList();
        }
        Map<String, List<String>> client = resourceAccess.get(clientId);
        if(client == null) {
            return Collections.emptyList();
        }
        List<String> roles = client.get(JwtClaims.ROLES);
        if(roles == null) {
            return Collections.emptyList();
        }
        return roles
              .stream()
              .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
              .collect(Collectors.toSet());
    }

    private String getPrincipleName(Jwt jwt) {
        return jwt.getClaim(JwtClaimNames.SUB);
    }
}

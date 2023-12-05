package com.syrnik.authprotocolsbackend.security.ldap;

import java.util.Collection;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;
import com.syrnik.authprotocolsbackend.security.CustomUserDetails;

@Component
public class LdapUserDetailsContextMapper extends LdapUserDetailsMapper {
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String email = ctx.getStringAttribute("mail");
        String firstName = ctx.getStringAttribute("givenName");
        String lastName = ctx.getStringAttribute("sn");
        return new CustomUserDetails(username, "", email, firstName, lastName, authorities);
    }
}

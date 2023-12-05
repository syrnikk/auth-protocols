package com.syrnik.authprotocolsbackend.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {
    private final String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private final Set<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public CustomUserDetails(String username, String password, String email, String firstName, String lastName,
          Collection<? extends GrantedAuthority> authorities) {
        this(username, password, email, firstName, lastName, true, true, true, true, authorities);
    }

    public CustomUserDetails(String username, String password, String email, String firstName, String lastName,
          boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
          Collection<? extends GrantedAuthority> authorities) {
        Assert.isTrue(username != null && !"".equals(username) && password != null,
              "Cannot pass null or empty values to constructor");
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities.stream().collect(Collectors.toUnmodifiableSet());
    }
}

package com.syrnik.authprotocolsbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.syrnik.authprotocolsbackend.security.CustomAuthenticationEntryPoint;
import com.syrnik.authprotocolsbackend.security.CustomLogoutSuccessHandler;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenFilter;
import com.syrnik.authprotocolsbackend.security.ldap.LdapAuthenticationSuccessHandler;
import com.syrnik.authprotocolsbackend.security.oidc.JwtAuthConverter;
import com.syrnik.authprotocolsbackend.security.oidc.OidcBearerTokenResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${app.saml.success-url}")
    private String samlSuccessUrl;

    @Value("${app.logout.success-url}")
    private String logoutSuccessUrl;

    private final JwtTokenFilter jwtTokenFilter;
    private final LdapAuthenticationSuccessHandler ldapAuthenticationSuccessHandler;
    private final OidcBearerTokenResolver oidcBearerTokenResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**")
                    .permitAll()
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new JwtAuthConverter()))
                    .bearerTokenResolver(oidcBearerTokenResolver))
              .saml2Login(saml2Login -> saml2Login.defaultSuccessUrl(samlSuccessUrl))
              .saml2Logout(Customizer.withDefaults())
              .formLogin(login -> login
                    .loginProcessingUrl("/api/login")
                    .successHandler(ldapAuthenticationSuccessHandler)
                    .failureHandler(new AuthenticationEntryPointFailureHandler(new CustomAuthenticationEntryPoint()))
                    .permitAll())
              .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler(logoutSuccessUrl)))
              .exceptionHandling(exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
              .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
              .build();
    }

    @Bean
    LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator authorities = new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        authorities.setGroupSearchFilter("(member={0})");
        return authorities;
    }

    @Bean
    AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchBase("ou=users");
        factory.setUserSearchFilter("(uid={0})");
        return factory.createAuthenticationManager();
    }
}

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
import com.syrnik.authprotocolsbackend.security.CustomAuthenticationEntryPoint;
import com.syrnik.authprotocolsbackend.security.CustomAuthenticationSuccessHandler;
import com.syrnik.authprotocolsbackend.security.CustomLogoutSuccessHandler;
import com.syrnik.authprotocolsbackend.security.oidc.JwtAuthConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${app.saml.success-url}")
    private String samlSuccessUrl;

    @Value("${app.logout.success-url}")
    private String logoutSuccessUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(
                    auth -> auth
                          .requestMatchers("/api/public/**").permitAll()
                          .anyRequest().authenticated())
              .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                    jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new JwtAuthConverter())))
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
              .saml2Login(saml2Login -> saml2Login.defaultSuccessUrl(samlSuccessUrl))
              .saml2Logout(Customizer.withDefaults())
              .formLogin(login -> login
                    .loginProcessingUrl("/api/login")
                    .successHandler(new CustomAuthenticationSuccessHandler())
                    .failureHandler(new AuthenticationEntryPointFailureHandler(new CustomAuthenticationEntryPoint()))
                    .permitAll())
              .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler(logoutSuccessUrl)))
              .exceptionHandling(exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
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

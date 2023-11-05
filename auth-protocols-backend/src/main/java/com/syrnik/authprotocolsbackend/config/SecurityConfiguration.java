package com.syrnik.authprotocolsbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.syrnik.authprotocolsbackend.security.CustomAuthenticationEntryPoint;
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
              .logout(logout -> logout.logoutSuccessHandler(new CustomLogoutSuccessHandler(logoutSuccessUrl)))
              .exceptionHandling(exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
              .build();
    }
}

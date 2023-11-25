package com.syrnik.authprotocolsbackend.config;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.GlobalSunJaasKerberosConfig;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.syrnik.authprotocolsbackend.security.CustomAuthenticationEntryPoint;
import com.syrnik.authprotocolsbackend.security.CustomLogoutSuccessHandler;
import com.syrnik.authprotocolsbackend.security.jwt.JwtTokenFilter;
import com.syrnik.authprotocolsbackend.security.ldap.LdapAuthenticationSuccessHandler;
import com.syrnik.authprotocolsbackend.security.oidc.OidcJwtConverter;
import com.syrnik.authprotocolsbackend.security.oidc.OidcBearerTokenResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.kerberos.spn}")
    private String servicePrincipalName;

    @Value("${app.kerberos.krb-conf-location}")
    private String krbConfLocation;

    private final JwtTokenFilter jwtTokenFilter;
    private final LdapAuthenticationSuccessHandler ldapAuthenticationSuccessHandler;
    private final OidcBearerTokenResolver oidcBearerTokenResolver;

    @Bean
    public SecurityFilterChain spnegoFilterChain(HttpSecurity http) throws Exception {
        http
              .securityMatcher("/api/kerberos/**")
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/kerberos/**").authenticated()
              )
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .exceptionHandling(exception -> exception.authenticationEntryPoint(spnegoEntryPoint()))
              .addFilterBefore(spnegoAuthenticationProcessingFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**")
                    .permitAll()
                    .requestMatchers("/api/saml2/**")
                    .permitAll()
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new OidcJwtConverter()))
                    .bearerTokenResolver(oidcBearerTokenResolver))
              .formLogin(login -> login
                    .loginProcessingUrl("/api/login")
                    .successHandler(ldapAuthenticationSuccessHandler)
                    .failureHandler(new AuthenticationEntryPointFailureHandler(new CustomAuthenticationEntryPoint()))
                    .permitAll())
              .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler(frontendUrl)))
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

    @Bean
    public GlobalSunJaasKerberosConfig jaasKrb5Config() {
        GlobalSunJaasKerberosConfig config = new GlobalSunJaasKerberosConfig();
        config.setKrbConfLocation(krbConfLocation);
        return config;
    }

    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
        SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
        ticketValidator.setServicePrincipal(servicePrincipalName);
        ticketValidator.setKeyTabLocation(new ClassPathResource("kerberos/krb5.keytab"));
        ticketValidator.setDebug(true);
        return ticketValidator;
    }

    public SpnegoEntryPoint spnegoEntryPoint() {
        return new SpnegoEntryPoint();
    }

    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter() {
        SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(spnegoAuthenticationManager());
        return filter;
    }

    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
        KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(sunJaasKerberosTicketValidator());
        provider.setUserDetailsService(kerberosUserDetailsService());
        return provider;
    }

    public UserDetailsService kerberosUserDetailsService() {
        return username -> new User(username, "", Collections.emptyList());
    }

    public AuthenticationManager spnegoAuthenticationManager() {
        return new ProviderManager(kerberosServiceAuthenticationProvider());
    }
}

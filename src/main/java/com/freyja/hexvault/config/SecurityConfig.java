package com.freyja.hexvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/assets/**", "/login/**", "/api/autocomplete", "/register").permitAll() // Allow public access to these paths
                        .requestMatchers(HttpMethod.POST, "/login", "/api/autocomplete", "/api/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register/approve", "/add-device", "/create-po", "/delete-note/", "/add-note/", "/add-part/").authenticated()
                        .requestMatchers("/", "/devices").hasAnyAuthority("user", "admin")
                        .anyRequest().authenticated() // Secure all other requestss
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page URL
                        .defaultSuccessUrl("/", true) // Redirect here after successful login
                        .failureUrl("/login?error=true") // Redirect here on failure
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Enable CSRF
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()) // Store CSRF token in request attribute
                )
                .logout(LogoutConfigurer::permitAll) // Permit all users to access logout
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // Prevent session fixation attacks
                        .maximumSessions(1) // Limit the number of sessions per user
                        .expiredUrl("/login") // Redirect to login page on session expiration
                );

        return http.build();
    }

}
package com.freyja.hexvault.config;

import com.freyja.hexvault.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Autowired private UserRepository userRepository;

    @Autowired private DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/assets/**", "/login/**", "/autocomplete", "/register").permitAll() // Allow public access to these paths
                        .requestMatchers(HttpMethod.POST, "/login", "/autocomplete", "/api/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register/approve", "/add-device", "/create-po").authenticated()
                        .anyRequest().authenticated() // Secure all other requests
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page URL
                        .defaultSuccessUrl("/", true) // Redirect here after successful login
                        .failureUrl("/login?error=true") // Redirect here on failure
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .logout(LogoutConfigurer::permitAll) // Permit all users to access logout
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // Prevent session fixation attacks
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
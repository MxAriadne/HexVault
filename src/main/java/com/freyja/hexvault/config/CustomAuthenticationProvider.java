/*
 * CustomAuthenticationProvider.java
 *
 * Custom authentication provider for Spring Security.
 * Overrides the base auth and retrieves the user & pass from the database.
 * Once confirmed, assigns the user an authentication token.
 *
 */

package com.freyja.hexvault.config;

import com.freyja.hexvault.entities.User;
import com.freyja.hexvault.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    //The goal of this class is to overwrite the SpringSecurity user data service and implement our own using the database.
    @Service
    static class DBUserService implements UserDetailsService {

        @Autowired private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not present"));
        }

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Instantiate the user details service
    private DBUserService userDetailsService;

    // Instantiate the password encoder
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Get username and password
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Load user by username
        User user = (User) userDetailsService.loadUserByUsername(username);

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Create authentication token
        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
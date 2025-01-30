package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.User;
import com.freyja.hexvault.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(HttpSession session) {
        session.setAttribute(
                "error", "SPRING_SECURITY_LAST_EXCEPTION"
        );
        return "/login";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        return "register";
    }

    @GetMapping("/account/logout")
    public String userLogout(HttpSecurity http) throws Exception {
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter());
        http.logout((logout) -> logout.addLogoutHandler(clearSiteData));
        return "redirect:/";
    }

}
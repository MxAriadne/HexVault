package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.User;
import com.freyja.hexvault.repos.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserManagementController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

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

    @PostMapping("/api/register")
    public String requestAccount() {

        // TODO: Make a new table on the database for notifications and send one to admins for them to approve, this would forward to api/register/approve

        return "redirect:/";
    }

    @PostMapping("/api/register/approve")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String firstName, @RequestParam String lastName) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPassword(passwordEncoder.encode(password));
        userRepository.save(u);
        return "redirect:/";
    }
}

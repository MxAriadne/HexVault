package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.Audit;
import com.freyja.hexvault.entities.PurchaseOrder;
import com.freyja.hexvault.entities.User;
import com.freyja.hexvault.repos.AuditRepository;
import com.freyja.hexvault.repos.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Controller
public class UserManagementController {

    @Autowired private UserRepository userRepository;
    @Autowired private AuditRepository auditRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuditRepository auditRepo;

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

        Audit audit = new Audit();
        audit.setActionTaken("Approved a new user account creation, new user is: " + username);
        audit.setTimestamp(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        audit.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        auditRepo.save(audit);

        return "redirect:/";
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"))) {

            List<Audit> audits = (List<Audit>) auditRepository.findAll();
            audits.sort(Comparator.comparing(Audit::getTimestamp).reversed());

            model.addAttribute("audits", audits);
            return "auditlog";
        } else {
            return "redirect:/register";
        }

    }

}

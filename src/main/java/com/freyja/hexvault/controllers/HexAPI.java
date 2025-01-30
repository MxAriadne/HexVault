package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.*;
import com.freyja.hexvault.repos.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RestController
public class HexAPI {

    @Autowired private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private CustomerRepository customerRepo;

    @Autowired private DeviceRepository deviceRepo;

    @Autowired private SKURepository skuRepo;

    @Autowired private PartsRepository partsRepo;

    @PostMapping("/api")
    public String api() {
        return "This is a test endpoint.";
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

    @PostMapping("/add-device")
    public String addDevice(HttpServletResponse httpResponse, @RequestParam String deviceName, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String desc, @RequestParam String deviceId, @RequestParam String email, @RequestParam String phone) throws IOException {
        Customer c = null;

        Collection<Customer> customers = (Collection<Customer>) customerRepo.findAll();
        if (customers.stream().noneMatch(customer -> customer.getEmail().equals(email))) {
            c = new Customer();
            c.setCustomerName(firstName + " " + lastName);
            c.setEmail(email);
            c.setPhoneNumber(phone);
            customerRepo.save(c);
        } else {
            Optional<Customer> temp = customers.stream().filter(customer1 -> customer1.getEmail().equals(email)).findFirst();
            if (temp.isPresent()) {
                c = temp.get();
            }
        }

        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setCustomer(c);
        device.setDescription(desc);
        device.setDeviceNum(deviceId);
        device.setStatus("Awaiting Repair");
        device.setTimestamp(Instant.now());
        deviceRepo.save(device);

        httpResponse.sendRedirect("/devices");
        return null;
    }

    @GetMapping("/autocomplete")
    public List<Map<String, String>> autocomplete(@RequestParam String query, @RequestParam String type) {

        switch (type) {
            case "phone", "email" -> {
                List<Customer> customers = (List<Customer>) customerRepo.findAll();

                return customers.stream()
                        .filter(customer -> (type.equals("email") && customer.getEmail().contains(query)) ||
                                (type.equals("phone") && customer.getPhoneNumber().contains(query)))
                        .map(customer -> Map.of(
                                "name", customer.getCustomerName(),
                                "email", customer.getEmail(),
                                "phoneNumber", customer.getPhoneNumber()
                        )).limit(5)
                        .toList();
            }
            case "part" -> {
                List<PartsIndividual> parts = (List<PartsIndividual>) partsRepo.findAll();
                return parts.stream()
                        .filter(part -> part.getId().toString().contains(query) || part.getPartSku().getPartName().contains(query))
                        .map(part -> Map.of(
                                "partNumber", part.getId().toString(),
                                "partName", part.getPartSku().getPartName()
                        )).limit(5)
                        .toList();
            }
            case "sku" -> {
                List<PartsSku> parts = (List<PartsSku>) skuRepo.findAll();
                return parts.stream()
                        .filter(part -> part.getId().toString().contains(query) || part.getPartName().contains(query))
                        .map(part -> Map.of(
                                "partNumber", part.getId().toString(),
                                "partName", part.getPartName()
                        )).limit(5)
                        .toList();
            }
            case null, default -> {
                return Collections.emptyList();
            }
        }
    }
}

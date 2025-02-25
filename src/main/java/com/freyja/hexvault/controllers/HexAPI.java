package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.Customer;
import com.freyja.hexvault.entities.PartsIndividual;
import com.freyja.hexvault.entities.PartsSku;
import com.freyja.hexvault.repos.CustomerRepository;
import com.freyja.hexvault.repos.PartsRepository;
import com.freyja.hexvault.repos.SKURepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class HexAPI {

    @Autowired private CustomerRepository customerRepo;
    @Autowired private PartsRepository partsRepo;
    @Autowired private SKURepository skuRepo;

    @GetMapping("/api/autocomplete")
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
                System.out.println("Invalid type: " + type + "or no type provided.");
                return Collections.emptyList();
            }
        }
    }
}

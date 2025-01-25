package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.*;
import com.freyja.hexvault.repos.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private NotesRepository noteRepo;

    @Autowired
    private PartsRepository partsRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private SKURepository skuRepo;

    @GetMapping({"/", "/devices/ap", "/devices/ip", "/devices/nto", "/devices/rfp"})
    public String greeting(Model model, HttpServletRequest request) {

        String url = request.getRequestURI();

        Comparator<Device> comparator = new Comparator<Device>() {
            @Override
            public int compare(Device left, Device right) {
                if (left.getTimestamp() == null || right.getTimestamp() == null) {
                    return 0;
                }
                return left.getTimestamp().compareTo(right.getTimestamp());
            }
        };

        // Fetch the collections of devices based on their status
        Collection<Device> devices = (Collection<Device>) deviceRepo.findAll();
        Collection<Device> awaitingRepair = deviceRepo.findAllByStatusIsLike("Awaiting Repair");
        Collection<Device> inProgress = deviceRepo.findAllByStatusIsLike("In Progress");
        Collection<Device> complete = deviceRepo.findAllByStatusIsLike("Complete");
        Collection<Device> awaitingParts = deviceRepo.findAllByStatusIsLike("Awaiting Parts");
        Collection<Device> needToOrder = deviceRepo.findAllByStatusIsLike("Need To Order");

        // Sort the list by timestamp (ascending order)
        ((List<Device>) devices).sort(comparator);
        ((List<Device>) awaitingRepair).sort(comparator);
        ((List<Device>) inProgress).sort(comparator);
        ((List<Device>) complete).sort(comparator);
        ((List<Device>) awaitingParts).sort(comparator);
        ((List<Device>) needToOrder).sort(comparator);

        // Add all the collections to the model
        model.addAttribute("devices", devices);
        model.addAttribute("inprogress", inProgress);
        model.addAttribute("complete", complete);
        model.addAttribute("awaitingRepair", awaitingRepair);
        model.addAttribute("awaitingParts", awaitingParts);
        model.addAttribute("needToOrder", needToOrder);

        // Return the corresponding view based on the URL
        return switch (url) {
            case "/" -> "home";
            case "/devices/ap" -> "devices/ap";
            case "/devices/ip" -> "devices/ip";
            case "/devices/nto" -> "devices/nto";
            case "/devices/rfp" -> "devices/rfp";
            default ->

                // Fallback if none match
                    "error";
        };

    }

    @GetMapping("/devices/{id}")
    public String showDevice(@PathVariable Integer id, Model model) {
        Device device = deviceRepo.findById(id).get();
        Collection<Note> notes = noteRepo.findAllByDevice(device);
        Collection<PartsIndividual> parts = partsRepo.findAllByDevice(device);

        model.addAttribute("parts", parts);
        model.addAttribute("notes", notes);
        model.addAttribute("device", device);
        return "/devices/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteDevice(@PathVariable Integer id) {
        deviceRepo.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/add-device")
    public String addDevice() {
        return "add-device";
    }

    @PostMapping("/add-device")
    public String addDevice(@RequestParam String deviceName, @RequestParam String customerName, @RequestParam String deviceDescription, @RequestParam String deviceNum, @RequestParam String customerEmail, @RequestParam String customerPhone) {
        Customer c;

        Collection<Customer> customers = (Collection<Customer>) customerRepo.findAll();
        if (customers.stream().noneMatch(customer -> customer.getEmail().equals(customerEmail))) {
            c = new Customer();
            c.setCustomerName(customerName);
            c.setEmail(customerEmail);
            c.setPhoneNumber(customerPhone);
            customerRepo.save(c);
        } else {
            c = customers.stream().filter(customer1 -> customer1.getEmail().equals(customerEmail)).findFirst().get();
        }

        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setCustomer(c);
        device.setDescription(deviceDescription);
        device.setDeviceNum(deviceNum);
        device.setStatus("Awaiting Repair");
        device.setTimestamp(java.time.Instant.now());
        deviceRepo.save(device);

        return "redirect:/";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
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
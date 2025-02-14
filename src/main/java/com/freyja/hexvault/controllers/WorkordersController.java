package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.*;
import com.freyja.hexvault.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class WorkordersController {

    @Autowired private DeviceRepository deviceRepo;

    @Autowired private NotesRepository noteRepo;

    @Autowired private PartsRepository partsRepo;

    @Autowired private CustomerRepository customerRepo;

    @Autowired private SKURepository skuRepo;

    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/{type}")
    public String greeting(Model model, @PathVariable String type) {

        Comparator<Device> comparator = (left, right) -> {
            if (left.getTimestamp() == null || right.getTimestamp() == null) {
                return 0;
            }
            return left.getTimestamp().compareTo(right.getTimestamp());
        };

        Collection<Device> devices = switch (type) {
            case "awaiting-repair" -> deviceRepo.findAllByStatusIsLike("Awaiting Repair");
            case "in-progress" -> deviceRepo.findAllByStatusIsLike("In Progress");
            case "complete" -> deviceRepo.findAllByStatusIsLike("Complete");
            case "awaiting-parts" -> deviceRepo.findAllByStatusIsLike("Awaiting Parts");
            case "need-to-order" -> deviceRepo.findAllByStatusIsLike("Need To Order");
            default -> (Collection<Device>) deviceRepo.findAll();
        };

        // Sort the list by timestamp (ascending order)
        ((List<Device>) devices).sort(comparator);

        // Add all the collections to the model
        model.addAttribute("devices", devices);

        return "home";
    }

    @GetMapping("/view/{id}")
    public String showDevice(@PathVariable Integer id, Model model) {
        Device device = null;

        Optional<Device> temp = deviceRepo.findById(id);
        if (temp.isPresent()) {
            device = temp.get();
        }

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

}
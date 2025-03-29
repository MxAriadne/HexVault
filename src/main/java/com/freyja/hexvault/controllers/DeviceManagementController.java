package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.*;
import com.freyja.hexvault.repos.*;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.mapping.Selectable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class DeviceManagementController {

    @Autowired private PartsRepository partRepo;
    @Autowired private SKURepository skuRepo;
    @Autowired private DeviceRepository deviceRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private NotesRepository notesRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private NotesRepository noteRepo;
    @Autowired private AuditRepository auditRepo;

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @RequestParam BigDecimal price, @RequestParam String deviceId) {
        PartsIndividual part = partRepo.findById(id).get();
        part.setPrice(price);

        return "redirect:/devices/" + deviceId;
    }

    @PostMapping("/add-part/{id}")
    public String addPart(@PathVariable Integer id, @RequestParam String partInput) {
        try {
            Optional<PartsSku> optionalSku = skuRepo.findById(Integer.valueOf(partInput));
            if (optionalSku.isEmpty()) {
                return "redirect:/view/" + id + "?part-not-found=true";
            }
            PartsSku sku = optionalSku.get();

            Optional<Device> optionalDevice = deviceRepo.findById(id);
            if (optionalDevice.isEmpty()) {
                return "redirect:/view/" + id + "?device-not-found=true";
            }
            Device device = optionalDevice.get();

            if (Boolean.TRUE.equals(sku.getIsService())) {
                PartsIndividual p = new PartsIndividual();
                p.setDevice(device);
                p.setPartSku(sku);
                p.setPrice(BigDecimal.ZERO);
                partRepo.save(p);
                return "redirect:/view/" + id;
            } else {
                Collection<PartsIndividual> possibleParts = partRepo.findByPartSku(sku);
                for (PartsIndividual part : possibleParts) {
                    if (part.getDevice() == null) {
                        part.setDevice(device);
                        partRepo.save(part);  // Save the updated part
                        return "redirect:/view/" + id;
                    } else if (part.getDevice().getId().equals(id)) {
                        return "redirect:/view/" + id + "?part-already-exists=true";
                    }
                }

                // If no existing part was updated, create a new one
                PartsIndividual p = new PartsIndividual();
                p.setPartSku(sku);
                p.setDevice(device);
                partRepo.save(p);

                Audit audit = new Audit();
                audit.setActionTaken("Added parts to device: " + p.getDevice().getDeviceName());
                audit.setTimestamp(OffsetDateTime.from(Instant.now()));
                audit.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                auditRepo.save(audit);

                return "redirect:/view/" + id;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/view/" + id + "?part-mismatch-error=true";
        }
    }

    @PostMapping("/add-note/{id}")
    public String addNote(@PathVariable Integer id, String noteText, @RequestParam("status") String status) {
        User u = userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Device d = deviceRepo.findById(id).get();

        Note note = new Note();
        note.setNote(noteText);
        note.setStatus(status);
        note.setDevice(d);
        note.setTimestamp(Instant.now());
        note.setCreatedBy(u);
        notesRepo.save(note);

        d.setStatus(status);
        deviceRepo.save(d);

        if (status.equals("Complete")) {
            Collection<PartsIndividual> parts = partRepo.findAllByDevice(d);
            for (PartsIndividual part : parts) {
                part.setStatus("Sold");
                part.getPartSku().setQuantity(part.getPartSku().getQuantity() - 1);
            }
        }

        Audit audit = new Audit();
        audit.setActionTaken("Added notes to device: " + d.getDeviceName());
        audit.setTimestamp(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        audit.setUsername(u.getUsername());
        auditRepo.save(audit);

        return "redirect:/view/" + id;
    }

    @PostMapping("/delete-note/{id}")
    public String deleteNote(@PathVariable Integer id, Integer deviceId) {
        notesRepo.deleteById(id);

        Audit audit = new Audit();
        audit.setActionTaken("Removed notes from device: " + deviceId);
        audit.setTimestamp(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        audit.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        auditRepo.save(audit);

        return "redirect:/view/" + deviceId;
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

        Audit audit = new Audit();
        audit.setActionTaken("Checked in device: " + deviceId);
        audit.setTimestamp(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        audit.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        auditRepo.save(audit);

        httpResponse.sendRedirect("/devices");
        return null;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        model.addAttribute("numUsers", userRepo.count());
        model.addAttribute("numComplete", deviceRepo.findAllByStatusIsLike("Complete").size());

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
        Collection<PartsIndividual> parts = partRepo.findAllByDevice(device);

        model.addAttribute("parts", parts);
        model.addAttribute("notes", notes);
        model.addAttribute("device", device);
        return "/devices/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteDevice(@PathVariable Integer id) {
        deviceRepo.deleteById(id);

        Audit audit = new Audit();
        audit.setActionTaken("Deleted a workorder: " + id);
        audit.setTimestamp(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        audit.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        auditRepo.save(audit);

        return "redirect:/";
    }

    @GetMapping("/add-device")
    public String addDevice() {
        return "add-device";
    }


}

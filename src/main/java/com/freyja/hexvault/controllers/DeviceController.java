package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.Device;
import com.freyja.hexvault.entities.Note;
import com.freyja.hexvault.entities.PartsIndividual;
import com.freyja.hexvault.entities.User;
import com.freyja.hexvault.repos.DeviceRepository;
import com.freyja.hexvault.repos.NotesRepository;
import com.freyja.hexvault.repos.PartsRepository;
import com.freyja.hexvault.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.Instant;

@Controller
public class DeviceController {

    @Autowired
    private PartsRepository partRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotesRepository notesRepo;

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @RequestParam BigDecimal price, @RequestParam String deviceId) {
        PartsIndividual part = partRepo.findById(id).get();
        part.setPrice(price);

        return "redirect:/devices/" + id;
    }

    @PostMapping("/add-part/{id}")
    public String addPart(@PathVariable Integer id, @RequestParam String partInput) {
        try {
            PartsIndividual part = partRepo.findById(Integer.valueOf(partInput)).get();
            Device device = deviceRepo.findById(id).get();

            if (part.getDevice() != null) {
                return "redirect:/devices/" + id + "?part-use-error=true";
            } else {
                part.setDevice(device);
                partRepo.save(part);
                return "redirect:/devices/" + id;
            }

        } catch (Exception e) {
            return "redirect:/devices/" + id + "?part-mismatch-error=true";
        }


    }

    @PostMapping("/add-note/{id}")
    public String addNote(@PathVariable Integer id, String noteText) {
        User u = userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        Note note = new Note();
        note.setNote(noteText);
        note.setDevice(deviceRepo.findById(id).get());
        note.setTimestamp(Instant.now());
        note.setCreatedBy(u);
        notesRepo.save(note);

        return "redirect:/devices/view/" + id;
    }

    @PostMapping("/delete-note/{id}")
    public String deleteNote(@PathVariable Integer id, Integer deviceId) {
        notesRepo.deleteById(id);
        return "redirect:/devices/view/" + deviceId;
    }
}

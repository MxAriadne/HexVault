package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.PartsSku;
import com.freyja.hexvault.repos.PartsRepository;
import com.freyja.hexvault.repos.SKURepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PartsInventoryController {

    @Autowired
    private SKURepository skuRepo;

    @GetMapping("/parts")
    public String parts(Model model) {
        model.addAttribute("parts", skuRepo.findAll());
        return "parts";
    }

    @PostMapping("/create-part")
    public String createPart(@RequestParam String partName) {
        PartsSku sku = new PartsSku();
        sku.setPartName(partName);
        sku.setQuantity(0);
        skuRepo.save(sku);
        return "redirect:/parts";
    }

    @PostMapping("/delete-sku")
    public String deletePart(@RequestParam Integer partId) {
        skuRepo.deleteById(partId);
        return "redirect:/parts";
    }
}

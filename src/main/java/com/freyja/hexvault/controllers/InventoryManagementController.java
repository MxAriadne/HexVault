package com.freyja.hexvault.controllers;

import com.freyja.hexvault.entities.*;
import com.freyja.hexvault.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Controller
public class InventoryManagementController {

    @Autowired private PurchaseOrderRepository purchaseOrderRepo;
    @Autowired private POItemRepository poItemRepo;
    @Autowired private SKURepository skuRepo;
    @Autowired private PartsRepository partsRepo;
    @Autowired private CustomerRepository customerRepo;

    @GetMapping("/inventory")
    public String parts(Model model) {
        model.addAttribute("parts", skuRepo.findAll());
        return "parts";
    }

    @PostMapping("/create-part")
    public String createPart(@RequestParam String partName, String limited) {
        if (limited == null) {
            limited = "";
        }
        PartsSku sku = new PartsSku();
        sku.setPartName(partName);
        sku.setIsService(limited.equals("Is this a service?"));
        sku.setQuantity(0);
        skuRepo.save(sku);
        return "redirect:/inventory";
    }

    @PostMapping("/delete-sku")
    public String deletePart(@RequestParam Integer partId) {
        skuRepo.deleteById(partId);
        return "redirect:/inventory";
    }

    @GetMapping("/purchasing")
    public String purchaseOrder(Model model) {

        List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) purchaseOrderRepo.findAll();

        purchaseOrders.sort(Comparator.comparing(PurchaseOrder::getTimestamp).reversed());

        model.addAttribute("purchaseOrders", purchaseOrders);

        return "purchasing";
    }

    @GetMapping("/po-details/{poId}")
    public String createPurchaseOrder(Model model, @PathVariable(required = false) Integer poId) {
        Optional<PurchaseOrder> temp = purchaseOrderRepo.findById(poId);
        PurchaseOrder po = null;

        if (temp.isPresent()) {
            po = temp.get();
        }

        model.addAttribute("po", po);
        model.addAttribute("parts", poItemRepo.findAllByPoId(poId));
        return "po-details";
    }

    @PostMapping("/create-po")
    public String createPurchaseOrder(String retailer, String orderNo) {
        PurchaseOrder po = new PurchaseOrder();

        po.setTimestamp(Instant.now());
        po.setRetailer(retailer);
        po.setStatus("Not finalized");
        po.setOrderNo(orderNo);
        po.setTotalPrice(BigDecimal.valueOf(0.00));
        purchaseOrderRepo.save(po);

        return "redirect:/purchasing";
    }

    @PostMapping("/add-part-po")
    public String addPartToPO(Integer poId, String partNumber, String quantity, Double price, String partName) {

        PoItem poItem = new PoItem();
        poItem.setPo(purchaseOrderRepo.findById(poId).get());
        poItem.setPartSku(skuRepo.findById(Integer.valueOf(partNumber)).get());
        poItem.setQuantity(Integer.valueOf(quantity));
        poItem.setPrice(BigDecimal.valueOf(price));
        poItemRepo.save(poItem);
        return "redirect:/po-details/" + poId;
    }

    @PostMapping("/delete-po/{poId}")
    public String deletePO(@PathVariable(required = false) Integer poId) {
        purchaseOrderRepo.deleteById(poId);
        return "redirect:/purchasing";
    }

    @PostMapping("/remove-part/")
    public String removePart(Integer poId, Integer partId) {
        poItemRepo.deleteById(partId);
        return "redirect:/po-details/" + poId;
    }

    @GetMapping("/finalize-po/{poId}")
    public String finalizePO(@PathVariable(required = false) Integer poId) {
        PurchaseOrder po = purchaseOrderRepo.findById(poId).get();
        po.setStatus("Finalized");
        purchaseOrderRepo.save(po);

        List<PoItem> parts = poItemRepo.findAllByPoId(poId);
        for (PoItem part : parts) {
            for (int i = 0; i < part.getQuantity(); i++) {
                PartsIndividual partsIndividual = new PartsIndividual();
                partsIndividual.setPartSku(part.getPartSku());
                partsIndividual.getPartSku().setQuantity(part.getQuantity() + partsIndividual.getPartSku().getQuantity());
                partsIndividual.setPrice(part.getPrice());
                partsRepo.save(partsIndividual);
            }

            po.setTotalPrice(BigDecimal.valueOf(po.getTotalPrice().doubleValue() + (part.getPrice().doubleValue() * part.getQuantity())));
        }
        purchaseOrderRepo.save(po);

        return "redirect:/purchasing";
    }
}

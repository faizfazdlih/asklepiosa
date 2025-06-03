package Kesehatan.Asklepios.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.service.ConsultationService;
import Kesehatan.Asklepios.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/consultations")
public class AdminConsultationController {

    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private UserService userService;

    // Menampilkan semua konsultasi
    @GetMapping
    public String listConsultations(Model model) {
        List<Consultation> consultations = consultationService.getAllConsultations();
        model.addAttribute("consultations", consultations);
        return "admin/consultations/list";
    }

    // Menampilkan detail konsultasi
    @GetMapping("/{id}")
    public String viewConsultation(@PathVariable String id, Model model) {
        Consultation consultation = consultationService.getById(id);
        model.addAttribute("consultation", consultation);
        model.addAttribute("statuses", Consultation.Status.values());
        return "admin/consultations/detail";
    }

    // Update status konsultasi
    @PostMapping("/{id}/update-status")
    public String updateConsultationStatus(
            @PathVariable String id,
            @RequestParam("status") Consultation.Status status,
            @RequestParam(value = "notes", required = false) String notes) {
        
        Consultation consultation = consultationService.getById(id);
        
        // Update status dan catatan
        consultation.setStatus(status);
        if (notes != null && !notes.trim().isEmpty()) {
            consultation.setNotes(notes);
        }
        
        consultationService.updateStatus(id, status);
        return "redirect:/admin/consultations/" + id;
    }
}
package Kesehatan.Asklepios.controller.psychologist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.ConsultationService;
import Kesehatan.Asklepios.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/psychologist/consultations")
public class PsychologistConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private UserService userService;

    // Daftar konsultasi untuk psikolog
    @GetMapping
    public String listConsultations(Model model, Principal principal) {
        String email = principal.getName();
        User psychologist = userService.getByEmail(email);
        
        // Ambil semua konsultasi yang terkait dengan psikolog ini
        List<Consultation> consultations = consultationService.getAllConsultations().stream()
                .filter(c -> c.getSchedule().getPsychologist().getUser().getId().equals(psychologist.getId()))
                .toList();
        
        model.addAttribute("consultations", consultations);
        return "psychologist/consultations/list";
    }

    // Detail konsultasi
    @GetMapping("/{id}")
    public String viewConsultation(@PathVariable String id, Model model, Principal principal) {
        String email = principal.getName();
        User psychologist = userService.getByEmail(email);
        
        Consultation consultation = consultationService.getById(id);
        
        // Pastikan konsultasi ini terkait dengan psikolog yang sedang login
        if (!consultation.getSchedule().getPsychologist().getUser().getId().equals(psychologist.getId())) {
            return "redirect:/psychologist/consultations";
        }
        
        model.addAttribute("consultation", consultation);
        model.addAttribute("statuses", Consultation.Status.values());
        return "psychologist/consultations/detail";
    }

    // Update status konsultasi
    @PostMapping("/{id}/update-status")
    public String updateConsultationStatus(
            @PathVariable String id,
            @RequestParam("status") Consultation.Status status,
            @RequestParam(value = "notes", required = false) String notes,
            Principal principal) {
        
        String email = principal.getName();
        User psychologist = userService.getByEmail(email);
        
        Consultation consultation = consultationService.getById(id);
        
        // Pastikan konsultasi ini terkait dengan psikolog yang sedang login
        if (!consultation.getSchedule().getPsychologist().getUser().getId().equals(psychologist.getId())) {
            return "redirect:/psychologist/consultations";
        }
        
        // Update status dan catatan
        consultation.setStatus(status);
        if (notes != null && !notes.trim().isEmpty()) {
            consultation.setNotes(notes);
        }
        
        consultationService.updateStatus(id, status);
        return "redirect:/psychologist/consultations/" + id;
    }
}
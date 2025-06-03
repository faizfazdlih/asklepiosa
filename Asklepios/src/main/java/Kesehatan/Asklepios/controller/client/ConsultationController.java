package Kesehatan.Asklepios.controller.client;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.ConsultationService;
import Kesehatan.Asklepios.service.ScheduleService;
import Kesehatan.Asklepios.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/client/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    // Tampilkan semua konsultasi untuk client yang login
    @GetMapping
    public String listConsultations(Model model, Principal principal) {
        String email = principal.getName();
        User client = userService.getAllUsers().stream()
                          .filter(u -> u.getEmail().equals(email))
                          .findFirst()
                          .orElseThrow(() -> new RuntimeException("User not found"));

        List<Consultation> consultations = consultationService.getConsultationsByClient(client.getId());
        model.addAttribute("consultations", consultations);
        return "client/consultations/list"; // sesuaikan dengan nama template HTML kamu
    }

    // Form pemesanan konsultasi berdasarkan schedule
    @GetMapping("/book/{scheduleId}")
    public String showBookingForm(@PathVariable String scheduleId, Model model) {
        Schedule schedule = scheduleService.getById(scheduleId);
        model.addAttribute("schedule", schedule);
        return "client/consultations/book"; // Ubah dari "consultation/book" ke "client/consultations/book"
    }

    // Simpan booking konsultasi
    @PostMapping("/book")
    public String bookConsultation(@RequestParam String scheduleId, Principal principal) {
        String email = principal.getName();
        User client = userService.getAllUsers().stream()
                          .filter(u -> u.getEmail().equals(email))
                          .findFirst()
                          .orElseThrow(() -> new RuntimeException("User not found"));

        consultationService.bookConsultation(scheduleId, client.getId());
        return "redirect:/client/consultations";
    }
}


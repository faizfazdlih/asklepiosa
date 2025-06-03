package Kesehatan.Asklepios.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.PsychologistProfileService;
import Kesehatan.Asklepios.service.ScheduleService;
import Kesehatan.Asklepios.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/client/dashboard")
public class ClientDashboardController {

    @Autowired
    private PsychologistProfileService psychologistProfileService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    // Halaman dashboard client
    @GetMapping
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        User client = userService.getByEmail(email);
        model.addAttribute("user", client);
        
        // Ambil daftar psikolog untuk ditampilkan
        List<PsychologistProfile> psychologists = psychologistProfileService.getAll();
        model.addAttribute("psychologists", psychologists);
        
        return "client/dashboard/index";
    }

    // Lihat detail psikolog
    @GetMapping("/psychologist/{id}")
    public String viewPsychologist(@PathVariable String id, Model model) {
        PsychologistProfile psychologist = psychologistProfileService.getById(id);
        model.addAttribute("psychologist", psychologist);
        
        // Ambil jadwal yang tersedia untuk psikolog ini
        List<Schedule> availableSchedules = scheduleService.getSchedulesByPsychologist(id);
        model.addAttribute("schedules", availableSchedules);
        
        return "client/dashboard/psychologist_detail";
    }
}
package Kesehatan.Asklepios.controller.psychologist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.repository.ConsultationRepository;
import Kesehatan.Asklepios.repository.PsychologistProfileRepository;
import Kesehatan.Asklepios.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/psychologist/dashboard")
public class PsychologistDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private PsychologistProfileRepository profileRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    // Dashboard psikolog
    @GetMapping
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Ambil profil psikolog
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        if (profile == null) {
            model.addAttribute("error", "Profil psikolog belum dibuat");
            return "psychologist/dashboard/index";
        }
        
        // Ambil konsultasi yang terkait dengan psikolog ini
        List<Consultation> consultations = consultationRepository.findBySchedulePsychologistUserId(psychologistUser.getId());
        
        model.addAttribute("profile", profile);
        model.addAttribute("consultations", consultations);
        model.addAttribute("upcomingCount", consultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.CONFIRMED)
                .count());
        model.addAttribute("completedCount", consultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.COMPLETED)
                .count());
        
        return "psychologist/dashboard/index";
    }
}
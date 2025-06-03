package Kesehatan.Asklepios.controller.psychologist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.repository.PsychologistProfileRepository;
import Kesehatan.Asklepios.service.PsychologistProfileService;
import Kesehatan.Asklepios.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/psychologist/profile")
public class PsychologistProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PsychologistProfileService profileService;

    @Autowired
    private PsychologistProfileRepository profileRepository;

    // Lihat profil psikolog
    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Cari profil psikolog berdasarkan user ID
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        model.addAttribute("user", psychologistUser);
        model.addAttribute("profile", profile);
        
        return "psychologist/profile/view";
    }

    // Form edit profil
    @GetMapping("/edit")
    public String showEditForm(Model model, Principal principal) {
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Cari profil psikolog berdasarkan user ID
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        // Jika belum ada profil, buat baru
        if (profile == null) {
            profile = new PsychologistProfile();
            profile.setUser(psychologistUser);
        }
        
        model.addAttribute("user", psychologistUser);
        model.addAttribute("profile", profile);
        
        return "psychologist/profile/edit";
    }

    // Submit update profil
    @PostMapping("/edit")
    public String updateProfile(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam("licenseNumber") String licenseNumber,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "experienceYears", required = false, defaultValue = "0") int experienceYears,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "profilePicture", required = false) String profilePicture,
            Principal principal) {
        
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        PsychologistProfile profile;
        
        // Jika ID kosong, buat profil baru
        if (id == null || id.isEmpty()) {
            profile = new PsychologistProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setUser(psychologistUser);
        } else {
            // Jika ada ID, ambil profil yang sudah ada
            profile = profileService.getById(id);
            
            // Pastikan profil ini milik psikolog yang sedang login
            if (!profile.getUser().getId().equals(psychologistUser.getId())) {
                return "redirect:/psychologist/profile";
            }
        }
        
        // Update data profil
        profile.setLicenseNumber(licenseNumber);
        profile.setSpecialization(specialization);
        profile.setExperienceYears(experienceYears);
        profile.setPrice(price);
        profile.setBio(bio);
        profile.setProfilePicture(profilePicture);
        
        profileService.save(profile);
        
        return "redirect:/psychologist/profile";
    }
}
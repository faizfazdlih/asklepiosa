package Kesehatan.Asklepios.controller.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.model.User.Role;
import Kesehatan.Asklepios.repository.PsychologistProfileRepository;
import Kesehatan.Asklepios.repository.UserRepository;

@Controller
@RequestMapping("/admin/psychologists")
public class PsychologistManagementController {

    @Autowired
    private PsychologistProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listPsychologists(Model model) {
        List<PsychologistProfile> list = profileRepository.findAll();
        model.addAttribute("psychologists", list);
        return "admin/psychologists/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("psychologist", new PsychologistProfile());
    
        // Ambil user dengan role CLIENT saja (yang belum menjadi psikolog)
        List<User> users = userRepository.findByRole(Role.CLIENT);
        model.addAttribute("users", users);
    
        return "admin/psychologists/create";
    }

    @PostMapping("/save")
    public String savePsychologist(@ModelAttribute PsychologistProfile psychologist,
                                @RequestParam("userId") String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        
        // Pastikan user yang dipilih adalah CLIENT
        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Hanya user dengan role CLIENT yang dapat dijadikan psikolog");
        }
        
        // Ubah role user menjadi PSYCHOLOGIST
        user.setRole(Role.PSYCHOLOGIST);
        userRepository.save(user);
        
        psychologist.setUser(user);

        // Generate ID jika belum ada
        if (psychologist.getId() == null || psychologist.getId().isEmpty()) {
            psychologist.setId(UUID.randomUUID().toString());
        }

        profileRepository.save(psychologist);
        return "redirect:/admin/psychologists";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        PsychologistProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid psychologist ID"));
        
        // Untuk edit, tampilkan semua user PSYCHOLOGIST dan current user
        List<User> users = userRepository.findByRole(Role.PSYCHOLOGIST);
        model.addAttribute("users", users);
        model.addAttribute("psychologist", profile);
        return "admin/psychologists/edit";
    }

    @PostMapping("/update")
    public String updatePsychologist(@RequestParam("id") String id,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("licenseNumber") String licenseNumber,
                                    @RequestParam(value = "specialization", required = false) String specialization,
                                    @RequestParam(value = "experienceYears", required = false, defaultValue = "0") int experienceYears,
                                    @RequestParam("price") BigDecimal price,
                                    @RequestParam(value = "bio", required = false) String bio,
                                    @RequestParam(value = "profilePicture", required = false) String profilePicture) {

        PsychologistProfile psychologist = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psikolog tidak ditemukan"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        // Set ulang data dari form
        psychologist.setUser(user);
        psychologist.setLicenseNumber(licenseNumber);
        psychologist.setSpecialization(specialization);
        psychologist.setExperienceYears(experienceYears);
        psychologist.setPrice(price);
        psychologist.setBio(bio);
        psychologist.setProfilePicture(profilePicture);

        profileRepository.save(psychologist);

        return "redirect:/admin/psychologists";
    }

    @GetMapping("/delete/{id}")
    public String deletePsychologist(@PathVariable String id) {
        // Ambil profile yang akan dihapus
        PsychologistProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psikolog tidak ditemukan"));
        
        // Ubah role user kembali menjadi CLIENT
        User user = profile.getUser();
        user.setRole(Role.CLIENT);
        userRepository.save(user);
        
        // Hapus profile psikolog
        profileRepository.deleteById(id);
        return "redirect:/admin/psychologists";
    }
}
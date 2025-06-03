package Kesehatan.Asklepios.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/client/profile")
public class ClientProfileController {

    @Autowired
    private UserService userService;

    // Menampilkan profil client
    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        String email = principal.getName();
        User client = userService.getByEmail(email);
        model.addAttribute("user", client);
        return "client/profile/edit";
    }

    // Form edit profil
    @GetMapping("/edit")
    public String showEditForm(Model model, Principal principal) {
        String email = principal.getName();
        User client = userService.getByEmail(email);
        model.addAttribute("user", client);
        model.addAttribute("genders", User.Gender.values());
        return "client/profile/edit";
    }

    // Submit update profil
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        String email = principal.getName();
        User existing = userService.getByEmail(email);
        
        // Update data yang diizinkan untuk diubah oleh client
        existing.setFullName(updatedUser.getFullName());
        existing.setPhone(updatedUser.getPhone());
        existing.setGender(updatedUser.getGender());
        // Email dan password tidak diubah di sini
        
        userService.save(existing);
        return "redirect:/client/profile";
    }
}
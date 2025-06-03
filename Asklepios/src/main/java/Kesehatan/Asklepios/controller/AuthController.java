package Kesehatan.Asklepios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Email atau password salah!");
        }
        if (logout != null) {
            model.addAttribute("message", "Anda telah berhasil logout!");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String fullName,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String gender,
                          Model model) {
        try {
            // Cek apakah email sudah ada
            try {
                userService.getByEmail(email);
                model.addAttribute("error", "Email sudah terdaftar!");
                return "auth/register";
            } catch (RuntimeException e) {
                // Email belum ada, lanjutkan registrasi
            }

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(User.Role.CLIENT); // Default role
            
            if (gender != null && !gender.isEmpty()) {
                user.setGender(User.Gender.valueOf(gender.toUpperCase()));
            }
            
            user.setCreatedAt(LocalDateTime.now());

            userService.save(user);
            model.addAttribute("message", "Registrasi berhasil! Silakan login.");
            return "auth/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan saat registrasi: " + e.getMessage());
            return "auth/register";
        }
    }
}
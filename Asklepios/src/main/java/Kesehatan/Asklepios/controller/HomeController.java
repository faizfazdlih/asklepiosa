package Kesehatan.Asklepios.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

        @GetMapping("/")
        public String home(Authentication authentication) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "landing"; // tampilkan landing.html untuk user belum login
            }

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                return "redirect:/client/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PSYCHOLOGIST"))) {
                return "redirect:/psychologist/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            }

            return "landing"; // fallback jika tidak punya role apa pun
        }

}
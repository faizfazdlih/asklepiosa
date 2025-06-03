package Kesehatan.Asklepios.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    // Menampilkan semua user di halaman HTML
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/user_list"; // file src/main/resources/templates/admin/user_list.html
    }

    // Menampilkan detail user tertentu
    @GetMapping("/{id}")
    public String viewUser(@PathVariable String id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user/user_detail"; // file src/main/resources/templates/admin/user_detail.html
    }

    // Menghapus user
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // Form edit user
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("genders", User.Gender.values());
        return "admin/user/user_edit"; // HTML edit user
    }

    // Submit update user
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable String id, @ModelAttribute("user") User updatedUser) {
        User existing = userService.getUserById(id);
        existing.setFullName(updatedUser.getFullName());
        existing.setPhone(updatedUser.getPhone());
        existing.setGender(updatedUser.getGender());
        existing.setRole(updatedUser.getRole());
        // Email dan password tidak diubah di sini
        userService.save(existing);
        return "redirect:/admin/users/" + id;
    }

    // Form tambah user
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("genders", User.Gender.values());
        return "admin/user/user_add";
    }

    // Submit tambah user
    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") User newUser) {
        newUser.setId(UUID.randomUUID().toString());
        newUser.setCreatedAt(LocalDateTime.now());
        // Pastikan password dienkripsi jika nanti digunakan
        userService.save(newUser);
        return "redirect:/admin/users";
    }
}


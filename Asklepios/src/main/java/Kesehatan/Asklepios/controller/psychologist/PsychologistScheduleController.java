package Kesehatan.Asklepios.controller.psychologist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.repository.PsychologistProfileRepository;
import Kesehatan.Asklepios.service.ScheduleService;
import Kesehatan.Asklepios.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/psychologist/schedules")
public class PsychologistScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PsychologistProfileRepository profileRepository;

    // Daftar jadwal psikolog
    @GetMapping
    public String listSchedules(Model model, Principal principal) {
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Cari profil psikolog berdasarkan user ID
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        if (profile == null) {
            model.addAttribute("error", "Profil psikolog belum dibuat");
            return "psychologist/schedules/list";
        }
        
        // Ambil semua jadwal untuk psikolog ini
        List<Schedule> schedules = scheduleService.getAll().stream()
                .filter(s -> s.getPsychologist().getId().equals(profile.getId()))
                .toList();
        
        model.addAttribute("schedules", schedules);
        return "psychologist/schedules/list";
    }

    // Form tambah jadwal
    @GetMapping("/add")
    public String showAddForm(Model model, Principal principal) {
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Cari profil psikolog berdasarkan user ID
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        if (profile == null) {
            model.addAttribute("error", "Profil psikolog belum dibuat");
            return "redirect:/psychologist/profile/edit";
        }
        
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("profile", profile);
        return "psychologist/schedules/add";
    }

    // Submit tambah jadwal
    @PostMapping("/add")
    public String addSchedule(
            @RequestParam("date") LocalDate date,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("endTime") LocalTime endTime,
            Principal principal,
            Model model) {
        
        String email = principal.getName();
        User psychologistUser = userService.getByEmail(email);
        
        // Cari profil psikolog berdasarkan user ID
        PsychologistProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(psychologistUser.getId()))
                .findFirst()
                .orElse(null);
        
        if (profile == null) {
            model.addAttribute("error", "Profil psikolog belum dibuat");
            return "redirect:/psychologist/profile/edit";
        }
        
        // Cek apakah sudah ada jadwal yang dibooking di tanggal yang sama
        boolean hasBooked = scheduleService.existsBookedScheduleForPsychologistAndDate(
                profile.getId(), date);
        
        if (hasBooked) {
            model.addAttribute("error", "Anda sudah memiliki jadwal yang dibooking di tanggal tersebut");
            model.addAttribute("schedule", new Schedule());
            model.addAttribute("profile", profile);
            return "psychologist/schedules/add";
        }
        
        // Buat jadwal baru
        Schedule schedule = new Schedule();
        schedule.setId(UUID.randomUUID().toString());
        schedule.setPsychologist(profile);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setIsBooked(false);
        
        scheduleService.save(schedule);
        
        return "redirect:/psychologist/schedules";
    }
}
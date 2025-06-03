package Kesehatan.Asklepios.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.service.PsychologistProfileService;
import Kesehatan.Asklepios.service.ScheduleService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/schedules")
public class ScheduleManagementController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PsychologistProfileService psychologistProfileService;

    // Tampilkan semua jadwal
    @GetMapping
    public String listSchedules(Model model) {
        List<Schedule> schedules = scheduleService.getAll();
        model.addAttribute("schedules", schedules);
        return "admin/schedule/list";
    }

    // Form tambah jadwal
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("psychologists", psychologistProfileService.getAll());
        return "admin/schedule/create";
    }

    // Submit tambah jadwal
    @PostMapping("/add")
    public String addSchedule(@ModelAttribute("schedule") Schedule schedule, Model model) {
        boolean hasBooked = scheduleService.existsBookedScheduleForPsychologistAndDate(
                schedule.getPsychologist().getId(),
                schedule.getDate()
        );

        if (hasBooked) {
            model.addAttribute("error", "Jadwal sudah terpesan untuk tanggal tersebut. Tidak dapat menambahkan jadwal.");
            model.addAttribute("schedule", schedule);
            model.addAttribute("psychologists", psychologistProfileService.getAll());
            return "admin/schedule/create";
        }

        schedule.setId(UUID.randomUUID().toString());
        schedule.setIsBooked(false); // default: belum dipesan
        scheduleService.save(schedule);
        return "redirect:/admin/schedules";
    }

    // Hapus jadwal
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable String id) {
        scheduleService.deleteById(id);
        return "redirect:/admin/schedules";
    }

    // Form edit
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Schedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            model.addAttribute("error", "Jadwal tidak ditemukan");
            return "redirect:/admin/schedules";
        }
        model.addAttribute("schedule", schedule);
        model.addAttribute("psychologists", psychologistProfileService.getAll());
        return "admin/schedule/edit";
    }

    // Submit edit
    @PostMapping("/edit/{id}")
    public String updateSchedule(@PathVariable String id, @ModelAttribute("schedule") Schedule updatedSchedule) {
        Schedule existingSchedule = scheduleService.getById(id);
        if (existingSchedule == null) {
            return "redirect:/admin/schedules";
        }

        existingSchedule.setPsychologist(updatedSchedule.getPsychologist());
        existingSchedule.setDate(updatedSchedule.getDate());
        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        // Jangan ubah isBooked
        scheduleService.save(existingSchedule);
        return "redirect:/admin/schedules";
    }

}

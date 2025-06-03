package Kesehatan.Asklepios.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedulesByPsychologist(String psychologistId) {
        return scheduleRepository.findByPsychologistIdAndIsBookedFalse(psychologistId);
    }

    public Schedule getById(String id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public Schedule save(Schedule schedule) {
        // Cek apakah psikolog ini sudah punya jadwal yang dibooking di tanggal yang sama
        boolean isAlreadyBookedToday = existsBookedScheduleForPsychologistAndDate(
            schedule.getPsychologist().getId(), schedule.getDate()
        );
    
        if (isAlreadyBookedToday) {
            throw new RuntimeException("Psikolog ini sudah memiliki jadwal yang dibooking di tanggal tersebut.");
        }
    
        return scheduleRepository.save(schedule);
    }
    

    public boolean existsBookedScheduleForPsychologistAndDate(String psychologistId, LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findByPsychologistIdAndDateAndIsBookedTrue(psychologistId, date);
        return !schedules.isEmpty();
    }

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }
    
    public void deleteById(String id) {
        scheduleRepository.deleteById(id);
    }

}
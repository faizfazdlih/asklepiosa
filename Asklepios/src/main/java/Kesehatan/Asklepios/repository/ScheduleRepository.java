package Kesehatan.Asklepios.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import Kesehatan.Asklepios.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByPsychologistIdAndDateAndIsBookedFalse(String psychologistId, LocalDate date);
    List<Schedule> findByPsychologistIdAndIsBookedFalse(String psychologistId);
    List<Schedule> findByPsychologistIdAndIsBookedTrue(String psychologistId);
    List<Schedule> findByPsychologistIdAndDateAndIsBookedTrue(String psychologistId, LocalDate date);

}
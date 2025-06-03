package Kesehatan.Asklepios.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "psychologist_id", nullable = false)
    private PsychologistProfile psychologist;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(name = "is_booked")
    private Boolean isBooked = false;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public PsychologistProfile getPsychologist() { return psychologist; }
    public void setPsychologist(PsychologistProfile psychologist) { this.psychologist = psychologist; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getIsBooked() { return isBooked; }
    public void setIsBooked(Boolean isBooked) { this.isBooked = isBooked; }
}
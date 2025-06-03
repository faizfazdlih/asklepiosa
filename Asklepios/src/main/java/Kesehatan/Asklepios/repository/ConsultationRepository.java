package Kesehatan.Asklepios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Kesehatan.Asklepios.model.Consultation;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {
    List<Consultation> findByClientId(String clientId);
    List<Consultation> findBySchedulePsychologistUserId(String psychologistUserId);
}
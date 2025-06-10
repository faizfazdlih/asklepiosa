package Kesehatan.Asklepios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Kesehatan.Asklepios.model.Consultation;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {
    List<Consultation> findByClientId(String clientId);
    List<Consultation> findBySchedulePsychologistUserId(String psychologistUserId);

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.transaction WHERE c.client.id = :clientId")
    List<Consultation> findByClientIdWithTransaction(@Param("clientId") Long clientId);

}
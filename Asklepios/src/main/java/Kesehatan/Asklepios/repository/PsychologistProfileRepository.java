package Kesehatan.Asklepios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Kesehatan.Asklepios.model.PsychologistProfile;

import java.util.List;

public interface PsychologistProfileRepository extends JpaRepository<PsychologistProfile, String> {
    List<PsychologistProfile> findBySpecializationContainingIgnoreCase(String specialization);
}
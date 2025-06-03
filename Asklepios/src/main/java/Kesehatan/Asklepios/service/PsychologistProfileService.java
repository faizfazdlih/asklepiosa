package Kesehatan.Asklepios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Kesehatan.Asklepios.model.PsychologistProfile;
import Kesehatan.Asklepios.repository.PsychologistProfileRepository;

@Service
public class PsychologistProfileService {

    @Autowired
    private PsychologistProfileRepository profileRepository;

    public PsychologistProfile getById(String id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Psychologist profile not found"));
    }

    public List<PsychologistProfile> getAll() {
        return profileRepository.findAll();
    }

    public PsychologistProfile save(PsychologistProfile profile) {
        return profileRepository.save(profile);
    }
    
    public void deleteById(String id) {
        profileRepository.deleteById(id);
    }
}
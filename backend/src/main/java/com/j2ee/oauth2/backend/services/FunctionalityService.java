package com.j2ee.oauth2.backend.services;

import com.j2ee.oauth2.backend.models.Functionality;
import com.j2ee.oauth2.backend.repository.FunctionalityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FunctionalityService {

    private final FunctionalityRepository functionalityRepository;

    public FunctionalityService(FunctionalityRepository functionalityRepository) {
        this.functionalityRepository = functionalityRepository;
    }

    public List<Functionality> getAllFunctionalities() {
        return functionalityRepository.findAll();
    }

    public Functionality getFunctionalityById(Long id) {
        return functionalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonctionnalité non trouvée"));
    }

    public Functionality createFunctionality(Functionality functionality) {
        Optional<Functionality> existing = functionalityRepository.findByName(functionality.getName());
        if (existing.isPresent()) {
            throw new RuntimeException("Cette fonctionnalité existe déjà");
        }
        return functionalityRepository.save(functionality);
    }

    public Functionality updateFunctionality(Long id, Functionality updatedFunctionality) {
        Functionality functionality = functionalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonctionnalité non trouvée"));

        functionality.setName(updatedFunctionality.getName());
        return functionalityRepository.save(functionality);
    }

    public void deleteFunctionality(Long id) {
        if (!functionalityRepository.existsById(id)) {
            throw new RuntimeException("Fonctionnalité non trouvée");
        }
        functionalityRepository.deleteById(id);
    }
}

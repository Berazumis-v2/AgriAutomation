package org.STPP.AgriAutomation.api.services;

import org.STPP.AgriAutomation.api.repositories.PCSRepository;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlantCareSystemService {

    private final PCSRepository pcsRepository;

    public PlantCareSystemService(PCSRepository pcsRepository) {
        this.pcsRepository = pcsRepository;
    }

    public Iterable<PlantCareSystem> findAll() {
        return pcsRepository.findAll();
    }

    public Optional<PlantCareSystem> findById(int id) {
        return pcsRepository.findById(id);
    }

    public PlantCareSystem save(PlantCareSystem plantCareSystem) {
        return pcsRepository.save(plantCareSystem);
    }

    public void deleteById(int id) {
        pcsRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return pcsRepository.existsById(id);
    }
}

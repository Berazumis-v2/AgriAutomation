package org.STPP.AgriAutomation.api.services;

import org.STPP.AgriAutomation.api.repositories.PlantRepository;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class PlantService {

    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public Iterable<Plant> findAll() {
        return plantRepository.findAll();
    }

    public Optional<Plant> findById(int id) {
        return plantRepository.findById(id);
    }

    public Iterable<Plant> findAllBySensorId(int sensorId) {
        return plantRepository.findAllBySensorId(sensorId);
    }

    public Plant save(Plant plant) {
        return plantRepository.save(plant);
    }

    public void deleteById(int id) {
        plantRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return plantRepository.existsById(id);
    }

}

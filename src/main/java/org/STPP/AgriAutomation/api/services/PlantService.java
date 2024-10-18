package org.STPP.AgriAutomation.api.services;

import java.util.concurrent.atomic.AtomicInteger;

import org.STPP.AgriAutomation.data.models.Plant;
import org.STPP.AgriAutomation.data.models.Sensor;
import org.springframework.stereotype.Service;

@Service
public class PlantService {

    private final AtomicInteger idGenerator = new AtomicInteger(5);

    public PlantService() {}

    public Plant addPlant(Plant plant, Sensor sensor){
        plant.setId(idGenerator.incrementAndGet());
        plant.setSensor(sensor);
        sensor.getAssignedPlants().add(plant);
        return plant;
    }

    public Plant updatePlant(Plant updatedPlant) {
        Plant existingPlant = updatedPlant.getSensor().getAssignedPlants().stream()
                .filter(plant -> plant.getId() == updatedPlant.getId())
                .findFirst()
                .orElse(null);
        
        if (existingPlant != null) {
            existingPlant.setName(updatedPlant.getName());
        }
        return existingPlant;
    }

    public void removePlant(Plant plant) {
        Sensor sensor = plant.getSensor();
        if (sensor != null) {
            sensor.getAssignedPlants().remove(plant);
            plant.setSensor(null);
        }
    }

    public boolean plantExists(Sensor sensor, Integer plantId){
        return sensor.getAssignedPlants().stream().anyMatch(plant -> plant.getId() == plantId);
    }

    public Plant getPlant(Sensor sensor, Integer plantId) {
        return sensor.getAssignedPlants().stream()
                .filter(plant -> plant.getId() == plantId)
                .findFirst()
                .orElse(null);
    }
}

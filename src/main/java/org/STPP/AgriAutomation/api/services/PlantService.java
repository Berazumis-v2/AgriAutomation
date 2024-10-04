package org.STPP.AgriAutomation.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.STPP.AgriAutomation.data.models.Plant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PlantService {

    private final List<Plant> plantList;

    public PlantService() {
        plantList = new ArrayList<>();

        Plant plant1 = new Plant(1,"Rose");
        Plant plant2 = new Plant(2,"Dracaena trifasciata");
        Plant plant3 = new Plant(3,"Jade plant");
        Plant plant4 = new Plant(4,"Swiss sheese plant");
        Plant plant5 = new Plant(5,"Golden Pothos");

        plantList.addAll(Arrays.asList(plant1, plant2, plant3, plant4, plant5));
    }

    public Plant getPlant(Integer id){
        for(Plant plant : plantList)
        {
            if(plant.getId() == id)
            {
                return plant;
            }
        }
        return null;
    }

    public boolean plantExists(Integer id){
        return plantList.stream().anyMatch(plant -> plant.getId() == id);
    }

    public ResponseEntity<List<Plant>> getAllPlants(){
        return new ResponseEntity<>(plantList, HttpStatus.OK);
    }

    public Plant addPlant(Plant plant){
        plantList.add(plant);
        return plant;
    }

    public Plant updatePlant(Plant updatedPlant) {
        Plant existingPlant = getPlant(updatedPlant.getId());
        if (existingPlant != null) {
            existingPlant.setName(updatedPlant.getName());
        }
        return existingPlant;
    }

    public void removePlant(Integer id) {
        plantList.removeIf(plant -> plant.getId() == id);
    }

}

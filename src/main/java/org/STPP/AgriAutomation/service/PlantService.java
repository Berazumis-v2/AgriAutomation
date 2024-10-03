package org.STPP.AgriAutomation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.model.Plant;
import org.springframework.stereotype.Service;

@Service
public class PlantService {

    private List<Plant> plantList;

    public PlantService() {
        plantList = new ArrayList<>();

        Plant plant1 = new Plant(1,"Rose");
        Plant plant2 = new Plant(2,"Dracaena trifasciata");
        Plant plant3 = new Plant(3,"Jade plant");
        Plant plant4 = new Plant(4,"Swiss sheese plant");
        Plant plant5 = new Plant(5,"Golden Pothos");

        plantList.addAll(Arrays.asList(plant1, plant2, plant3, plant4, plant5));
    }

    public Optional<Plant> getUser(Integer id){
        Optional optional = Optional.empty();
        for(Plant plant : plantList)
        {
            if(plant.getId() == id)
            {
                optional = Optional.of(plant);
                return optional;
            }
        }
        return optional;
    }

}

package org.STPP.AgriAutomation.api.contoller;

import java.util.Optional;

import org.STPP.AgriAutomation.api.model.Plant;
import org.STPP.AgriAutomation.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlantController {
    private PlantService plantService;

    @Autowired 
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping("/plant")
    public Plant getUser(@RequestParam Integer id){

        Optional user = plantService.getUser(id);
        if (user.isPresent())
            return (Plant) user.get();
        else
        return null;

    }

}

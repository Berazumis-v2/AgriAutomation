package org.STPP.AgriAutomation.api.contoller;

import java.util.List;

import org.STPP.AgriAutomation.Data.Entities.Plant;
import org.STPP.AgriAutomation.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plants")
public class PlantController {
    private PlantService plantService;

    @Autowired 
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants(){
        return plantService.getAllPlants();
    }

     @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlant(@PathVariable Integer id){
        Plant plant = plantService.getPlant(id);
        if(plant != null){
            return ResponseEntity.ok(plant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Plant> createPlant(@RequestBody Plant plant){
        Plant createdPlant = plantService.addPlant(plant);
        return new ResponseEntity<>(createdPlant, HttpStatus.CREATED);
    }

     @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable Integer id, @RequestBody Plant updatedPlant){
        if(plantService.plantExists(id)) {
            updatedPlant.setId(id);
            Plant plant = plantService.updatePlant(updatedPlant);
            return ResponseEntity.ok(plant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Integer id) {
        if(plantService.plantExists(id)) {
            plantService.removePlant(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

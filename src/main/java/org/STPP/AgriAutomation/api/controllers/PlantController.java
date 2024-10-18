package org.STPP.AgriAutomation.api.controllers;

import java.util.List;

import org.STPP.AgriAutomation.api.services.PlantService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.data.models.Plant;
import org.STPP.AgriAutomation.data.models.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/sensors/{sensorId}")
public class PlantController {
    private final PlantService plantService;
    private final SensorService sensorService;

    @Autowired 
    public PlantController(PlantService plantService, SensorService sensorService) {
        this.plantService = plantService;
        this.sensorService = sensorService;
    }

    @GetMapping("/plants")
    public ResponseEntity<List<Plant>> getPlantsBySensor(@PathVariable Integer sensorId) {
        Sensor sensor = sensorService.getSensor(sensorId);
        if (sensor != null) {
            return ResponseEntity.ok(sensor.getAssignedPlants());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/plants/{plantId}")
    public ResponseEntity<Plant> getPlantBySensor(@PathVariable Integer sensorId, @PathVariable Integer plantId) {
        Sensor sensor = sensorService.getSensor(sensorId);
        if (sensor != null) {
            Plant plant = plantService.getPlant(sensor, plantId);
            if (plant != null) {
                return ResponseEntity.ok(plant);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<Plant> assignPlantToSensor(@PathVariable Integer sensorId, @RequestBody Plant plant) {
        Sensor sensor = sensorService.getSensor(sensorId);
        if (sensor != null) {
            if (!plantService.plantExists(sensor, plant.getId())) {
                plantService.addPlant(plant, sensor);
            } else {
                plant = plantService.getPlant(sensor, plant.getId());
            }
            return ResponseEntity.status(201).body(plant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("plants/{plantId}")
    public ResponseEntity<Plant> updatePlantUnderSensor(@PathVariable Integer sensorId, @PathVariable Integer plantId, @RequestBody Plant updatedPlant) {
        Sensor sensor = sensorService.getSensor(sensorId);
        if (sensor != null && plantService.plantExists(sensor, plantId)) {
            updatedPlant.setId(plantId);
            updatedPlant.setSensor(sensor);
            Plant plant = plantService.updatePlant(updatedPlant);
            return ResponseEntity.ok(plant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("plants/{plantId}")
    public ResponseEntity<Void> removePlantFromSensor(@PathVariable Integer sensorId, @PathVariable Integer plantId) {
        Sensor sensor = sensorService.getSensor(sensorId);
        if (sensor != null && plantService.plantExists(sensor, plantId)) {
            Plant plant = plantService.getPlant(sensor, plantId);
            plantService.removePlant(plant);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

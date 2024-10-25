package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;
import org.STPP.AgriAutomation.api.services.PlantService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.data.dtos.PlantConverter;
import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/plantcaresystems/{plantcaresystemId}/sensors/{sensorId}/plants")
@Validated
public class PlantController {

    private final PlantService plantService;
    private final SensorService sensorService;
    private final PlantCareSystemService plantCareSystemService;
    private final Logger logger = LoggerFactory.getLogger(PlantController.class);

    public PlantController(PlantService plantService, SensorService sensorService, PlantCareSystemService plantCareSystemService) {
        this.plantService = plantService;
        this.sensorService = sensorService;
        this.plantCareSystemService = plantCareSystemService;
    }

    @GetMapping
    public ResponseEntity<List<PlantResponseDTO>> findAll(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {
        logger.info("Finding all plants for Sensor with id: {} and PlantCareSystem with id: {}", sensorId, plantcaresystemId);

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        List<Plant> plants = (List<Plant>) plantService.findAllBySensorId(sensorId);
        List<PlantResponseDTO> responseDTOs = PlantConverter.convertToResponseDTOList(plants);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<PlantResponseDTO> findById(@PathVariable int plantcaresystemId, @PathVariable int sensorId, @PathVariable int plantId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        Plant plant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + plantId + " not found"));

        if (plant.getSensor() == null || plant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant with id " + plantId + " not found for Sensor with id " + sensorId);
        }

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(plant);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PlantResponseDTO> create(
            @PathVariable int plantcaresystemId,
            @PathVariable int sensorId,
            @Valid @RequestBody PlantRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        Plant plant = PlantConverter.convertToEntity(requestDTO);
        plant.setSensor(sensor);

        Plant savedPlant = plantService.save(plant);

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(savedPlant);

        URI location = uriBuilder.path("/plantcaresystems/{plantcaresystemId}/sensors/{sensorId}/plants/{plantId}")
                .buildAndExpand(plantcaresystemId, sensorId, savedPlant.getId()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{plantId}")
    public ResponseEntity<PlantResponseDTO> updateById(
            @PathVariable int plantcaresystemId,
            @PathVariable int sensorId,
            @PathVariable int plantId,
            @Valid @RequestBody PlantRequestDTO requestDTO) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        Plant existingPlant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + plantId + " not found"));

        if (existingPlant.getSensor() == null || existingPlant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant with id " + plantId + " not found for Sensor with id " + sensorId);
        }

        existingPlant.setName(requestDTO.getName());
        existingPlant.setGrowthStage(requestDTO.getGrowthStage());

        Plant updatedPlant = plantService.save(existingPlant);

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(updatedPlant);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{plantId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable int plantcaresystemId,
            @PathVariable int sensorId,
            @PathVariable int plantId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        Plant existingPlant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + plantId + " not found"));

        if (existingPlant.getSensor() == null || existingPlant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant with id " + plantId + " not found for Sensor with id " + sensorId);
        }

        plantService.deleteById(plantId);
        return ResponseEntity.noContent().build();
    }
}

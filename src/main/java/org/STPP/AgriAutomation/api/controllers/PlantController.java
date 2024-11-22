package org.STPP.AgriAutomation.api.controllers;

import java.net.URI;
import java.util.List;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.PlantService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.PlantConverter;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/plantcaresystems/{plantcaresystemId}/sensors/{sensorId}/plants")
@Validated
public class PlantController {

    private final PlantService plantService;
    private final SensorService sensorService;
    private final PlantCareSystemService plantCareSystemService;
    private final UserService userService; // New dependency

    public PlantController(PlantService plantService, SensorService sensorService, PlantCareSystemService plantCareSystemService, UserService userService) {
        this.plantService = plantService;
        this.sensorService = sensorService;
        this.plantCareSystemService = plantCareSystemService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PlantResponseDTO>> findAll(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        List<Plant> plants = (List<Plant>) plantService.findAllBySensorId(sensorId);
        List<PlantResponseDTO> responseDTOs = PlantConverter.convertToResponseDTOList(plants);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<PlantResponseDTO> findById(@PathVariable int plantcaresystemId, @PathVariable int sensorId, @PathVariable int plantId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        Plant plant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", "id", plantId));

        if (plant.getSensor() == null || plant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant", "sensorId", sensorId);
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
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        Plant plant = PlantConverter.convertToEntity(requestDTO, sensor, currentUser);
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
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        Plant existingPlant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", "id", plantId));

        if (existingPlant.getSensor() == null || existingPlant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant", "sensorId", sensorId);
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
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        Plant existingPlant = plantService.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant", "id", plantId));

        if (existingPlant.getSensor() == null || existingPlant.getSensor().getId() != sensorId) {
            throw new ResourceNotFoundException("Plant", "sensorId", sensorId);
        }

        plantService.deleteById(plantId);
        return ResponseEntity.noContent().build();
    }
}

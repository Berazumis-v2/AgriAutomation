package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;
import org.STPP.AgriAutomation.api.services.PlantService;
import org.STPP.AgriAutomation.api.services.SensorService;
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
@RequestMapping("/plantcaresystems/{plantcaresystemId}/sensors/{sensorID}/plants")
@Validated
public class PlantController {

    private final PlantService plantService;
    private final SensorService sensorService;
    private final Logger logger = LoggerFactory.getLogger(PlantController.class);

    public PlantController(PlantService plantService, SensorService sensorService) {
        this.plantService = plantService;
        this.sensorService = sensorService;
    }

    @GetMapping
    public ResponseEntity<List<PlantResponseDTO>> findAll() {
        logger.info("Finding all plants");
        List<Plant> plants = (List<Plant>) plantService.findAll();
        List<PlantResponseDTO> responseDTOs = PlantConverter.convertToResponseDTOList(plants);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> findById(@PathVariable int id) {
        logger.info("Finding plant with id: {}", id);
        Plant plant = plantService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + id + " not found"));

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(plant);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PlantResponseDTO> create(
            @Valid @RequestBody PlantRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating plant: {}", requestDTO);

        // Find the associated Sensor
        Sensor sensor = sensorService.findById(requestDTO.getSensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + requestDTO.getSensorId() + " not found"));

        Plant plant = PlantConverter.convertToEntity(requestDTO);
        plant.setSensor(sensor);

        Plant savedPlant = plantService.save(plant);

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(savedPlant);

        URI location = uriBuilder.path("/plants/{id}").buildAndExpand(savedPlant.getId()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantResponseDTO> updateById(
            @PathVariable int id,
            @Valid @RequestBody PlantRequestDTO requestDTO) {
        logger.info("Updating plant with id: {}", id);

        Plant existingPlant = plantService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant with id " + id + " not found"));

        // Update fields
        existingPlant.setName(requestDTO.getName());
        existingPlant.setGrowthStage(requestDTO.getGrowthStage());

        // Update the Sensor association if changed
        if (existingPlant.getSensor().getId() != requestDTO.getSensorId()) {
            Sensor sensor = sensorService.findById(requestDTO.getSensorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + requestDTO.getSensorId() + " not found"));
            existingPlant.setSensor(sensor);
        }

        Plant updatedPlant = plantService.save(existingPlant);

        PlantResponseDTO responseDTO = PlantConverter.convertToResponseDTO(updatedPlant);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        logger.info("Deleting plant with id: {}", id);
        if (!plantService.existsById(id)) {
            throw new ResourceNotFoundException("Plant with id " + id + " not found");
        }

        plantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

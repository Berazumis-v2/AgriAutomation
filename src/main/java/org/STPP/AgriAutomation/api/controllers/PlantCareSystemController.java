package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemResponseDTO;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemConverter;
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
@RequestMapping("/plantcaresystems")
@Validated
public class PlantCareSystemController {

    private final PlantCareSystemService plantCareSystemService;
    private final Logger logger = LoggerFactory.getLogger(PlantCareSystemController.class);

    public PlantCareSystemController(PlantCareSystemService plantCareSystemService) {
        this.plantCareSystemService = plantCareSystemService;
    }

    @GetMapping
    public ResponseEntity<List<PlantCareSystemResponseDTO>> findAll() {
        logger.info("Finding all plant care systems");
        List<PlantCareSystem> plantCareSystems = (List<PlantCareSystem>) plantCareSystemService.findAll();
        List<PlantCareSystemResponseDTO> responseDTOs = PlantCareSystemConverter.convertToResponseDTOList(plantCareSystems);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantCareSystemResponseDTO> findById(@PathVariable int id) {
        logger.info("Finding plant care system with id: {}", id);
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", id));

        PlantCareSystemResponseDTO responseDTO = PlantCareSystemConverter.convertToResponseDTO(plantCareSystem);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PlantCareSystemResponseDTO> create(
            @Valid @RequestBody PlantCareSystemRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating plant care system: {}", requestDTO);

        PlantCareSystem plantCareSystem = PlantCareSystemConverter.convertToEntity(requestDTO);
        PlantCareSystem savedPlantCareSystem = plantCareSystemService.save(plantCareSystem);

        PlantCareSystemResponseDTO responseDTO = PlantCareSystemConverter.convertToResponseDTO(savedPlantCareSystem);

        URI location = uriBuilder.path("/plantcaresystems/{id}").buildAndExpand(savedPlantCareSystem.getId()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantCareSystemResponseDTO> updateById(
            @PathVariable int id,
            @Valid @RequestBody PlantCareSystemRequestDTO requestDTO) {
        logger.info("Updating plant care system with id: {}", id);

        PlantCareSystem existingPlantCareSystem = plantCareSystemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", id));

        // Update fields
        existingPlantCareSystem.setName(requestDTO.getName());
        existingPlantCareSystem.setDescription(requestDTO.getDescription());
        existingPlantCareSystem.setAutomationEnabled(requestDTO.isAutomationEnabled());

        if (requestDTO.getMaintenanceTimeStamp() != null) {
            existingPlantCareSystem.setMaintenanceTimeStamp(requestDTO.getMaintenanceTimeStamp());
        }
        // If maintenanceTimeStamp is not provided, retain the existing value

        PlantCareSystem updatedPlantCareSystem = plantCareSystemService.save(existingPlantCareSystem);

        PlantCareSystemResponseDTO responseDTO = PlantCareSystemConverter.convertToResponseDTO(updatedPlantCareSystem);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        logger.info("Deleting plant care system with id: {}", id);
        if (!plantCareSystemService.existsById(id)) {
            throw new ResourceNotFoundException("PlantCareSystem", "id", id);
        }

        plantCareSystemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

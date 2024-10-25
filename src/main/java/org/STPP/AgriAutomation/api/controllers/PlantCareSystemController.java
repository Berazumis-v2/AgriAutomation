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

    public PlantCareSystemController(PlantCareSystemService plantCareSystemService) {
        this.plantCareSystemService = plantCareSystemService;
    }

    @GetMapping
    public ResponseEntity<List<PlantCareSystemResponseDTO>> findAll() {
        List<PlantCareSystem> plantCareSystems = (List<PlantCareSystem>) plantCareSystemService.findAll();
        List<PlantCareSystemResponseDTO> responseDTOs = PlantCareSystemConverter.convertToResponseDTOList(plantCareSystems);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantCareSystemResponseDTO> findById(@PathVariable int id) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", id));

        PlantCareSystemResponseDTO responseDTO = PlantCareSystemConverter.convertToResponseDTO(plantCareSystem);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PlantCareSystemResponseDTO> create(
            @Valid @RequestBody PlantCareSystemRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {

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

        PlantCareSystem existingPlantCareSystem = plantCareSystemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", id));

        existingPlantCareSystem.setName(requestDTO.getName());
        existingPlantCareSystem.setDescription(requestDTO.getDescription());
        existingPlantCareSystem.setAutomationEnabled(requestDTO.isAutomationEnabled());

        if (requestDTO.getMaintenanceTimeStamp() != null) {
            existingPlantCareSystem.setMaintenanceTimeStamp(requestDTO.getMaintenanceTimeStamp());
        }

        PlantCareSystem updatedPlantCareSystem = plantCareSystemService.save(existingPlantCareSystem);

        PlantCareSystemResponseDTO responseDTO = PlantCareSystemConverter.convertToResponseDTO(updatedPlantCareSystem);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        if (!plantCareSystemService.existsById(id)) {
            throw new ResourceNotFoundException("PlantCareSystem", "id", id);
        }

        plantCareSystemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

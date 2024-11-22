package org.STPP.AgriAutomation.api.controllers;

import java.net.URI;
import java.util.List;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemConverter;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemResponseDTO;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/plantcaresystems")
@Validated
public class PlantCareSystemController {

    private final PlantCareSystemService plantCareSystemService;
    private final UserService userService;

    public PlantCareSystemController(PlantCareSystemService plantCareSystemService, UserService userService) {
        this.plantCareSystemService = plantCareSystemService;
        this.userService = userService;
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

        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        PlantCareSystem plantCareSystem = PlantCareSystemConverter.convertToEntity(requestDTO, currentUser);
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

    // Retrieve the authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    User currentUser = userService.findByUsername(currentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

    // Check if the user is the creator or has admin role
    if (!existingPlantCareSystem.getCreatedBy().getId().equals(currentUser.getId()) &&
        !currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ADMIN))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Proceed with the update
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
    PlantCareSystem existingPlantCareSystem = plantCareSystemService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", id));

    // Retrieve the authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    User currentUser = userService.findByUsername(currentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

    // Check if the user is the creator or has admin role
    if (!existingPlantCareSystem.getCreatedBy().getId().equals(currentUser.getId()) &&
        !currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ADMIN))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    plantCareSystemService.deleteById(id);
    return ResponseEntity.noContent().build();
}
}

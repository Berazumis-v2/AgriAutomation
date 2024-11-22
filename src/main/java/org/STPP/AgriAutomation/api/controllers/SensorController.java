package org.STPP.AgriAutomation.api.controllers;

import java.net.URI;
import java.util.List;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.SensorConverter;
import org.STPP.AgriAutomation.data.dtos.SensorRequestDTO;
import org.STPP.AgriAutomation.data.dtos.SensorResponseDTO;
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
@RequestMapping("/plantcaresystems/{plantcaresystemId}/sensors")
@Validated
public class SensorController {

    private final SensorService sensorService;
    private final PlantCareSystemService plantCareSystemService;
    private final UserService userService; // New dependency

    public SensorController(SensorService sensorService, PlantCareSystemService plantCareSystemService, UserService userService) {
        this.sensorService = sensorService;
        this.plantCareSystemService = plantCareSystemService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<SensorResponseDTO>> findAll(@PathVariable int plantcaresystemId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        List<Sensor> sensors = (List<Sensor>) sensorService.findAllByPlantCareSystemId(plantcaresystemId);
        List<SensorResponseDTO> responseDTOs = SensorConverter.convertToResponseDTOList(sensors);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{sensorId}")
    public ResponseEntity<SensorResponseDTO> findById(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(sensor);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<SensorResponseDTO> create(
            @PathVariable int plantcaresystemId,
            @Valid @RequestBody SensorRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        Sensor sensor = SensorConverter.convertToEntity(requestDTO, plantCareSystem, currentUser);
        Sensor savedSensor = sensorService.save(sensor);

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(savedSensor);

        URI location = uriBuilder.path("/plantcaresystems/{plantcaresystemId}/sensors/{sensorId}")
                .buildAndExpand(plantcaresystemId, savedSensor.getId()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{sensorId}")
    public ResponseEntity<SensorResponseDTO> updateById(
            @PathVariable int plantcaresystemId,
            @PathVariable int sensorId,
            @Valid @RequestBody SensorRequestDTO requestDTO) {

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor existingSensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (existingSensor.getPlantCareSystem() == null || existingSensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        existingSensor.setModel(requestDTO.getModel());
        existingSensor.setTemperature(requestDTO.getTemperature());
        existingSensor.setHumidity(requestDTO.getHumidity());

        Sensor updatedSensor = sensorService.save(existingSensor);

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(updatedSensor);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{sensorId}")
    public ResponseEntity<Void> deleteById(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem", "id", plantcaresystemId));

        Sensor existingSensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", "id", sensorId));

        if (existingSensor.getPlantCareSystem() == null || existingSensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor", "plantCareSystemId", plantcaresystemId);
        }

        sensorService.deleteById(sensorId);
        return ResponseEntity.noContent().build();
    }
}

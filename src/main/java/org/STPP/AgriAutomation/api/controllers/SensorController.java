package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.dtos.SensorRequestDTO;
import org.STPP.AgriAutomation.data.dtos.SensorResponseDTO;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.data.dtos.SensorConverter;
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
@RequestMapping("/plantcaresystems/{plantcaresystemId}/sensors")
@Validated
public class SensorController {

    private final SensorService sensorService;
    private final PlantCareSystemService plantCareSystemService;
    private final Logger logger = LoggerFactory.getLogger(SensorController.class);

    public SensorController(SensorService sensorService, PlantCareSystemService plantCareSystemService) {
        this.sensorService = sensorService;
        this.plantCareSystemService = plantCareSystemService;
    }

    @GetMapping
    public ResponseEntity<List<SensorResponseDTO>> findAll(@PathVariable int plantcaresystemId) {
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        List<Sensor> sensors = (List<Sensor>) sensorService.findAllByPlantCareSystemId(plantcaresystemId);
        List<SensorResponseDTO> responseDTOs = SensorConverter.convertToResponseDTOList(sensors);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{sensorId}")
    public ResponseEntity<SensorResponseDTO> findById(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {
        logger.info("Finding sensor with id: {} for PlantCareSystem with id: {}", sensorId, plantcaresystemId);

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (sensor.getPlantCareSystem() == null || sensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(sensor);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<SensorResponseDTO> create(
            @PathVariable int plantcaresystemId,
            @Valid @RequestBody SensorRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating sensor for PlantCareSystem with id: {}", plantcaresystemId);

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor sensor = SensorConverter.convertToEntity(requestDTO);
        sensor.setPlantCareSystem(plantCareSystem);

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
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor existingSensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (existingSensor.getPlantCareSystem() == null || existingSensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        existingSensor.setModel(requestDTO.getModel());
        existingSensor.setTemperature(requestDTO.getTemperature());
        existingSensor.setHumidity(requestDTO.getHumidity());
        existingSensor.setReadingTimestamp(requestDTO.getReadingTimestamp());
        existingSensor.setCalibrationTimestamp(requestDTO.getCalibrationTimestamp());

        Sensor updatedSensor = sensorService.save(existingSensor);

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(updatedSensor);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{sensorId}")
    public ResponseEntity<Void> deleteById(@PathVariable int plantcaresystemId, @PathVariable int sensorId) {
        logger.info("Deleting sensor with id: {} for PlantCareSystem with id: {}", sensorId, plantcaresystemId);

        PlantCareSystem plantCareSystem = plantCareSystemService.findById(plantcaresystemId)
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + plantcaresystemId + " not found"));

        Sensor existingSensor = sensorService.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + sensorId + " not found"));

        if (existingSensor.getPlantCareSystem() == null || existingSensor.getPlantCareSystem().getId() != plantcaresystemId) {
            throw new ResourceNotFoundException("Sensor with id " + sensorId + " not found for PlantCareSystem with id " + plantcaresystemId);
        }

        sensorService.deleteById(sensorId);
        return ResponseEntity.noContent().build();
    }
}

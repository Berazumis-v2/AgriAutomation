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
    public ResponseEntity<List<SensorResponseDTO>> findAll() {
        logger.info("Finding all sensors");
        List<Sensor> sensors = (List<Sensor>) sensorService.findAll();
        List<SensorResponseDTO> responseDTOs = SensorConverter.convertToResponseDTOList(sensors);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponseDTO> findById(@PathVariable int id) {
        logger.info("Finding sensor with id: {}", id);
        Sensor sensor = sensorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + id + " not found"));

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(sensor);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<SensorResponseDTO> create(
            @Valid @RequestBody SensorRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating sensor: {}", requestDTO);

        // Find the associated PlantCareSystem
        PlantCareSystem plantCareSystem = plantCareSystemService.findById(requestDTO.getPlantCareSystemId())
                .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + requestDTO.getPlantCareSystemId() + " not found"));

        Sensor sensor = SensorConverter.convertToEntity(requestDTO);
        sensor.setPlantCareSystem(plantCareSystem);

        Sensor savedSensor = sensorService.save(sensor);

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(savedSensor);

        URI location = uriBuilder.path("/sensors/{id}").buildAndExpand(savedSensor.getId()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponseDTO> updateById(
            @PathVariable int id,
            @Valid @RequestBody SensorRequestDTO requestDTO) {
        logger.info("Updating sensor with id: {}", id);

        Sensor existingSensor = sensorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor with id " + id + " not found"));

        // Update fields
        existingSensor.setModel(requestDTO.getModel());
        existingSensor.setTemperature(requestDTO.getTemperature());
        existingSensor.setHumidity(requestDTO.getHumidity());
        existingSensor.setReadingTimestamp(requestDTO.getReadingTimestamp());
        existingSensor.setCalibrationTimestamp(requestDTO.getCalibrationTimestamp());

        // Update the PlantCareSystem association if changed
        if (existingSensor.getPlantCareSystem().getId() != requestDTO.getPlantCareSystemId()) {
            PlantCareSystem plantCareSystem = plantCareSystemService.findById(requestDTO.getPlantCareSystemId())
                    .orElseThrow(() -> new ResourceNotFoundException("PlantCareSystem with id " + requestDTO.getPlantCareSystemId() + " not found"));
            existingSensor.setPlantCareSystem(plantCareSystem);
        }

        Sensor updatedSensor = sensorService.save(existingSensor);

        SensorResponseDTO responseDTO = SensorConverter.convertToResponseDTO(updatedSensor);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        logger.info("Deleting sensor with id: {}", id);
        if (!sensorService.existsById(id)) {
            throw new ResourceNotFoundException("Sensor with id " + id + " not found");
        }

        sensorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package org.STPP.AgriAutomation.data.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;

public class SensorConverter {

    public static SensorResponseDTO convertToResponseDTO(Sensor sensor) {
        UserDTO userDTO = new UserDTO(
                sensor.getCreatedBy().getId(),
                sensor.getCreatedBy().getUsername()
        );

        return new SensorResponseDTO(
                sensor.getId(),
                sensor.getModel(),
                sensor.getTemperature(),
                sensor.getHumidity(),
                sensor.getReadingTimestamp(),
                sensor.getCalibrationTimestamp(),
                sensor.getPlantCareSystem().getId(),
                userDTO
        );
    }

    public static List<SensorResponseDTO> convertToResponseDTOList(List<Sensor> sensors) {
        return sensors.stream()
                .map(SensorConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static Sensor convertToEntity(SensorRequestDTO dto, PlantCareSystem plantCareSystem, User createdBy) {
        Sensor sensor = new Sensor();
        sensor.setModel(dto.getModel());
        sensor.setTemperature(dto.getTemperature());
        sensor.setHumidity(dto.getHumidity());
        sensor.setPlantCareSystem(plantCareSystem);
        sensor.setCreatedBy(createdBy);
        // Timestamps will be handled by lifecycle callbacks (@PrePersist, @PreUpdate)
        return sensor;
    }
}

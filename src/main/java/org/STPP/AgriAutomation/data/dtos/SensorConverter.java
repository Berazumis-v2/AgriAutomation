package org.STPP.AgriAutomation.data.dtos;

import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.dtos.SensorRequestDTO;
import org.STPP.AgriAutomation.data.dtos.SensorResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SensorConverter {

    public static SensorResponseDTO convertToResponseDTO(Sensor sensor) {
        return new SensorResponseDTO(
                sensor.getId(),
                sensor.getModel(),
                sensor.getTemperature(),
                sensor.getHumidity(),
                sensor.getReadingTimestamp(),
                sensor.getCalibrationTimestamp(),
                sensor.getPlantCareSystem().getId()
        );
    }

    public static List<SensorResponseDTO> convertToResponseDTOList(List<Sensor> sensors) {
        return sensors.stream()
                .map(SensorConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static Sensor convertToEntity(SensorRequestDTO dto) {
        Sensor sensor = new Sensor();
        sensor.setModel(dto.getModel());
        sensor.setTemperature(dto.getTemperature());
        sensor.setHumidity(dto.getHumidity());
        // Do not set readingTimestamp and calibrationTimestamp
        // The PlantCareSystem association will be set in the controller or service layer
        return sensor;
    }
}

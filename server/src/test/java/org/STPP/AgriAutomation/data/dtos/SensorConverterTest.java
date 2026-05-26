package org.STPP.AgriAutomation.data.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.Test;

class SensorConverterTest {

    @Test
    void convertToResponseDTOCopiesSensorFieldsAndRelationshipIds() {
        LocalDateTime calibration = LocalDateTime.of(2026, 5, 27, 8, 15);
        Sensor sensor = sensor(5, "TH-200", 21, 55, calibration, 12, "alice");
        sensor.prePersist();

        SensorResponseDTO dto = SensorConverter.convertToResponseDTO(sensor);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getModel()).isEqualTo("TH-200");
        assertThat(dto.getTemperature()).isEqualTo(21);
        assertThat(dto.getHumidity()).isEqualTo(55);
        assertThat(dto.getReadingTimestamp()).isNotNull();
        assertThat(dto.getCalibrationTimestamp()).isEqualTo(calibration);
        assertThat(dto.getPlantCareSystemId()).isEqualTo(12);
        assertThat(dto.getCreatedBy().getUsername()).isEqualTo("alice");
    }

    @Test
    void convertToEntityCopiesRequestFieldsAndAssociations() {
        LocalDateTime calibration = LocalDateTime.of(2026, 5, 27, 9, 0);
        SensorRequestDTO request = new SensorRequestDTO("Humidity probe", 19, 70, calibration);
        PlantCareSystem plantCareSystem = new PlantCareSystem();
        User creator = user("bob");

        Sensor sensor = SensorConverter.convertToEntity(request, plantCareSystem, creator);

        assertThat(sensor.getModel()).isEqualTo("Humidity probe");
        assertThat(sensor.getTemperature()).isEqualTo(19);
        assertThat(sensor.getHumidity()).isEqualTo(70);
        assertThat(sensor.getCalibrationTimestamp()).isEqualTo(calibration);
        assertThat(sensor.getPlantCareSystem()).isSameAs(plantCareSystem);
        assertThat(sensor.getCreatedBy()).isSameAs(creator);
    }

    @Test
    void convertToResponseDTOListMapsAllSensors() {
        LocalDateTime calibration = LocalDateTime.of(2026, 5, 27, 10, 0);
        Sensor first = sensor(1, "A1", 18, 45, calibration, 4, "chris");
        Sensor second = sensor(2, "B2", 22, 60, calibration, 4, "dana");
        first.prePersist();
        second.prePersist();

        List<SensorResponseDTO> dtos = SensorConverter.convertToResponseDTOList(List.of(first, second));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(SensorResponseDTO::getModel).containsExactly("A1", "B2");
    }

    private static Sensor sensor(
            int id,
            String model,
            int temperature,
            int humidity,
            LocalDateTime calibration,
            int plantCareSystemId,
            String username) {
        PlantCareSystem plantCareSystem = new PlantCareSystem();
        plantCareSystem.setId(plantCareSystemId);
        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setModel(model);
        sensor.setTemperature(temperature);
        sensor.setHumidity(humidity);
        sensor.setCalibrationTimestamp(calibration);
        sensor.setPlantCareSystem(plantCareSystem);
        sensor.setCreatedBy(user(username));
        return sensor;
    }

    private static User user(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}

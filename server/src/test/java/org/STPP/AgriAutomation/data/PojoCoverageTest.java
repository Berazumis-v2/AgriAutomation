package org.STPP.AgriAutomation.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;

import org.STPP.AgriAutomation.api.exceptions.ErrorDetail;
import org.STPP.AgriAutomation.api.exceptions.ErrorResponse;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemResponseDTO;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;
import org.STPP.AgriAutomation.data.dtos.SensorRequestDTO;
import org.STPP.AgriAutomation.data.dtos.SensorResponseDTO;
import org.STPP.AgriAutomation.data.dtos.UserDTO;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.RefreshToken;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.Session;
import org.STPP.AgriAutomation.data.entities.Student;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PojoCoverageTest {

    @Test
    void dtoNoArgConstructorsAndSettersStoreValues() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 27, 18, 0);
        UserDTO creator = new UserDTO();
        creator.setUsername("alice");

        PlantResponseDTO plantResponse = new PlantResponseDTO();
        plantResponse.setId(1);
        plantResponse.setName("Basil");
        plantResponse.setGrowthStage("Seedling");
        plantResponse.setSensorId(2);
        plantResponse.setCreatedBy(creator);

        SensorResponseDTO sensorResponse = new SensorResponseDTO();
        sensorResponse.setId(3);
        sensorResponse.setModel("S-100");
        sensorResponse.setTemperature(21);
        sensorResponse.setHumidity(60);
        sensorResponse.setReadingTimestamp(timestamp);
        sensorResponse.setCalibrationTimestamp(timestamp);
        sensorResponse.setPlantCareSystemId(4);
        sensorResponse.setCreatedBy(creator);

        PlantCareSystemResponseDTO systemResponse = new PlantCareSystemResponseDTO();
        systemResponse.setId(5);
        systemResponse.setName("Greenhouse");
        systemResponse.setDescription("North side");
        systemResponse.setAutomationEnabled(true);
        systemResponse.setMaintenanceTimeStamp(timestamp);
        systemResponse.setCreatedBy(creator);

        assertThat(plantResponse.getId()).isEqualTo(1);
        assertThat(plantResponse.getName()).isEqualTo("Basil");
        assertThat(plantResponse.getGrowthStage()).isEqualTo("Seedling");
        assertThat(plantResponse.getSensorId()).isEqualTo(2);
        assertThat(plantResponse.getCreatedBy()).isSameAs(creator);
        assertThat(sensorResponse.getId()).isEqualTo(3);
        assertThat(sensorResponse.getModel()).isEqualTo("S-100");
        assertThat(sensorResponse.getTemperature()).isEqualTo(21);
        assertThat(sensorResponse.getHumidity()).isEqualTo(60);
        assertThat(sensorResponse.getReadingTimestamp()).isEqualTo(timestamp);
        assertThat(sensorResponse.getCalibrationTimestamp()).isEqualTo(timestamp);
        assertThat(sensorResponse.getPlantCareSystemId()).isEqualTo(4);
        assertThat(sensorResponse.getCreatedBy()).isSameAs(creator);
        assertThat(systemResponse.getId()).isEqualTo(5);
        assertThat(systemResponse.getName()).isEqualTo("Greenhouse");
        assertThat(systemResponse.getDescription()).isEqualTo("North side");
        assertThat(systemResponse.isAutomationEnabled()).isTrue();
        assertThat(systemResponse.getMaintenanceTimeStamp()).isEqualTo(timestamp);
        assertThat(systemResponse.getCreatedBy()).isSameAs(creator);
    }

    @Test
    void dtoAllArgConstructorsStoreValues() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 27, 18, 30);
        UserDTO creator = new UserDTO("alice");

        PlantResponseDTO plant = new PlantResponseDTO(1, "Basil", "Seedling", 2, creator);
        SensorResponseDTO sensor = new SensorResponseDTO(3, "S-100", 21, 60, timestamp, timestamp, 4, creator);
        PlantCareSystemResponseDTO system =
                new PlantCareSystemResponseDTO(5, "Greenhouse", "North side", true, timestamp, creator);

        assertThat(plant.getCreatedBy().getUsername()).isEqualTo("alice");
        assertThat(sensor.getCalibrationTimestamp()).isEqualTo(timestamp);
        assertThat(system.isAutomationEnabled()).isTrue();
    }

    @Test
    void requestDtosExposeConstructorAndSetterValues() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 27, 19, 0);

        PlantRequestDTO plant = new PlantRequestDTO();
        plant.setName("Mint");
        plant.setGrowthStage("Vegetative");
        plant.setSensorId(7);
        SensorRequestDTO sensor = new SensorRequestDTO();
        sensor.setModel("Probe");
        sensor.setTemperature(22);
        sensor.setHumidity(55);
        sensor.setCalibrationTimestamp(timestamp);
        PlantCareSystemRequestDTO system = new PlantCareSystemRequestDTO();
        system.setName("Beds");
        system.setDescription("Raised beds");
        system.setAutomationEnabled(true);
        system.setMaintenanceTimeStamp(timestamp);

        assertThat(plant.getName()).isEqualTo("Mint");
        assertThat(plant.getGrowthStage()).isEqualTo("Vegetative");
        assertThat(plant.getSensorId()).isEqualTo(7);
        assertThat(sensor.getModel()).isEqualTo("Probe");
        assertThat(sensor.getTemperature()).isEqualTo(22);
        assertThat(sensor.getHumidity()).isEqualTo(55);
        assertThat(sensor.getCalibrationTimestamp()).isEqualTo(timestamp);
        assertThat(system.getName()).isEqualTo("Beds");
        assertThat(system.getDescription()).isEqualTo("Raised beds");
        assertThat(system.isAutomationEnabled()).isTrue();
        assertThat(system.getMaintenanceTimeStamp()).isEqualTo(timestamp);
    }

    @Test
    void authDtosExposeConstructorsSettersAndConversion() {
        AuthResponse response = new AuthResponse("access");
        response.setAccessToken("new-access");
        response.setRefreshToken("refresh");
        RegisterRequest register = new RegisterRequest();
        register.setUsername("alice");
        register.setPassword("password");
        LoginRequest login = new LoginRequest();
        ReflectionTestUtils.setField(login, "username", "alice");
        ReflectionTestUtils.setField(login, "password", "password");

        User user = register.toEntity();

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        assertThat(register.getUsername()).isEqualTo("alice");
        assertThat(register.getPassword()).isEqualTo("password");
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(login.getUsername()).isEqualTo("alice");
        assertThat(login.getPassword()).isEqualTo("password");
    }

    @Test
    void entityConstructorsSettersAndToStringStoreValues() {
        Instant expiry = Instant.now().plusSeconds(60);
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 27, 20, 0);
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("password");
        user.addRole(new Role(Role.USER));

        Role role = new Role();
        role.setId(2L);
        role.setName(Role.ADMIN);
        PlantCareSystem system = new PlantCareSystem("Greenhouse", "North side");
        system.setId(3);
        system.setAutomationEnabled(true);
        system.setMaintenanceTimeStamp(timestamp);
        system.setCreatedBy(user);
        Sensor sensor = new Sensor("S-100", system);
        sensor.setId(4);
        sensor.setTemperature(23);
        sensor.setHumidity(64);
        sensor.setCalibrationTimestamp(timestamp);
        sensor.setCreatedBy(user);
        Plant plant = new Plant(5, "Basil");
        plant.setGrowthStage("Seedling");
        plant.setSensor(sensor);
        plant.setCreatedBy(user);
        Session session = new Session();
        session.setUserId("1");
        session.setExpiresAt(expiry);
        session.setRevoked(true);
        RefreshToken refreshToken = new RefreshToken(user, expiry);
        refreshToken.setId(6L);
        refreshToken.setToken("refresh-token");
        refreshToken.setExpiryDate(expiry);
        refreshToken.setRevoked(true);
        refreshToken.setUser(user);
        Student student = new Student(7, "Bob", 10);
        student.setId(8);
        student.setName("Alice");
        student.setMarks(9);

        assertThat(role.getId()).isEqualTo(2L);
        assertThat(role.getName()).isEqualTo(Role.ADMIN);
        assertThat(system.getName()).isEqualTo("Greenhouse");
        assertThat(system.getDescription()).isEqualTo("North side");
        assertThat(system.isAutomationEnabled()).isTrue();
        assertThat(system.getMaintenanceTimeStamp()).isEqualTo(timestamp);
        assertThat(system.getCreatedBy()).isSameAs(user);
        assertThat(sensor.getId()).isEqualTo(4);
        assertThat(sensor.getModel()).isEqualTo("S-100");
        assertThat(sensor.getTemperature()).isEqualTo(23);
        assertThat(sensor.getHumidity()).isEqualTo(64);
        assertThat(sensor.getPlantCareSystem()).isSameAs(system);
        assertThat(sensor.getCalibrationTimestamp()).isEqualTo(timestamp);
        assertThat(sensor.getCreatedBy()).isSameAs(user);
        assertThat(plant.getId()).isEqualTo(5);
        assertThat(plant.getName()).isEqualTo("Basil");
        assertThat(plant.getGrowthStage()).isEqualTo("Seedling");
        assertThat(plant.getSensor()).isSameAs(sensor);
        assertThat(plant.getCreatedBy()).isSameAs(user);
        assertThat(session.getUserId()).isEqualTo("1");
        assertThat(session.getExpiresAt()).isEqualTo(expiry);
        assertThat(session.isRevoked()).isTrue();
        assertThat(refreshToken.getId()).isEqualTo(6L);
        assertThat(refreshToken.getToken()).isEqualTo("refresh-token");
        assertThat(refreshToken.getExpiryDate()).isEqualTo(expiry);
        assertThat(refreshToken.isRevoked()).isTrue();
        assertThat(refreshToken.getUser()).isSameAs(user);
        assertThat(user.toString()).contains("alice", "password");
        assertThat(student.getId()).isEqualTo(8);
        assertThat(student.getName()).isEqualTo("Alice");
        assertThat(student.getMarks()).isEqualTo(9);
        assertThat(student.toString()).contains("id=8", "name='Alice'", "marks=9");
    }

    @Test
    void errorModelsNoArgConstructorsAndSettersStoreValues() {
        ErrorDetail detail = new ErrorDetail();
        detail.setResource("Plant");
        detail.setField("name");
        detail.setCode("NotBlank");
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Validation Failed");
        response.setErrors(java.util.List.of(detail));

        assertThat(response.getMessage()).isEqualTo("Validation Failed");
        assertThat(response.getErrors()).containsExactly(detail);
        assertThat(detail.getResource()).isEqualTo("Plant");
        assertThat(detail.getField()).isEqualTo("name");
        assertThat(detail.getCode()).isEqualTo("NotBlank");
    }
}

package org.STPP.AgriAutomation.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.SensorRequestDTO;
import org.STPP.AgriAutomation.data.dtos.SensorResponseDTO;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class SensorControllerTest {

    @Mock
    private SensorService sensorService;

    @Mock
    private PlantCareSystemService plantCareSystemService;

    @Mock
    private UserService userService;

    private SensorController controller;

    @BeforeEach
    void setUp() {
        controller = new SensorController(sensorService, plantCareSystemService, userService);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("alice", null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllReturnsSensorsForPlantCareSystem() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor first = sensor(1, system, user(1L, "alice"));
        Sensor second = sensor(2, system, user(1L, "alice"));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findAllByPlantCareSystemId(4)).thenReturn(List.of(first, second));

        ResponseEntity<List<SensorResponseDTO>> response = controller.findAll(4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting(SensorResponseDTO::getId).containsExactly(1, 2);
    }

    @Test
    void findByIdRejectsSensorFromDifferentPlantCareSystem() {
        PlantCareSystem requestedSystem = plantCareSystem(4);
        PlantCareSystem otherSystem = plantCareSystem(5);
        Sensor sensor = sensor(9, otherSystem, user(1L, "alice"));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(requestedSystem));
        when(sensorService.findById(9)).thenReturn(Optional.of(sensor));

        assertThatThrownBy(() -> controller.findById(4, 9))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Sensor with plantCareSystemId '4' not found");
    }

    @Test
    void createBuildsSensorForAuthenticatedUser() {
        PlantCareSystem system = plantCareSystem(4);
        User creator = user(1L, "alice");
        SensorRequestDTO request = request("Probe A", 20, 60);
        Sensor saved = sensor(10, system, creator);
        saved.setModel("Probe A");
        saved.setTemperature(20);
        saved.setHumidity(60);
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));
        when(sensorService.save(org.mockito.ArgumentMatchers.any(Sensor.class))).thenReturn(saved);

        ResponseEntity<SensorResponseDTO> response =
                controller.create(4, request, UriComponentsBuilder.fromPath(""));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString())
                .isEqualTo("/plantcaresystems/4/sensors/10");
        assertThat(response.getBody().getModel()).isEqualTo("Probe A");
    }

    @Test
    void updateByIdAllowsCreatorAndSavesChangedFields() {
        PlantCareSystem system = plantCareSystem(4);
        User creator = user(1L, "alice");
        Sensor existing = sensor(9, system, creator);
        SensorRequestDTO request = request("Updated sensor", 25, 40);
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(9)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));
        when(sensorService.save(existing)).thenReturn(existing);

        ResponseEntity<SensorResponseDTO> response = controller.updateById(4, 9, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existing.getModel()).isEqualTo("Updated sensor");
        assertThat(existing.getTemperature()).isEqualTo(25);
        assertThat(existing.getHumidity()).isEqualTo(40);
        verify(sensorService).save(existing);
    }

    @Test
    void updateByIdReturnsForbiddenForNonCreatorNonAdmin() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor existing = sensor(9, system, user(1L, "alice"));
        User otherUser = user(2L, "bob");
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("bob", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(9)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("bob")).thenReturn(Optional.of(otherUser));

        ResponseEntity<SensorResponseDTO> response = controller.updateById(4, 9, request("Denied", 22, 55));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(sensorService, never()).save(existing);
    }

    @Test
    void deleteByIdAllowsAdminEvenWhenNotCreator() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor existing = sensor(9, system, user(1L, "alice"));
        User admin = user(2L, "admin", Role.ADMIN);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("admin", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(9)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("admin")).thenReturn(Optional.of(admin));

        ResponseEntity<Void> response = controller.deleteById(4, 9);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(sensorService).deleteById(9);
    }

    private static SensorRequestDTO request(String model, int temperature, int humidity) {
        return new SensorRequestDTO(model, temperature, humidity, LocalDateTime.of(2026, 5, 27, 17, 0));
    }

    private static Sensor sensor(int id, PlantCareSystem system, User creator) {
        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setModel("Sensor " + id);
        sensor.setTemperature(21);
        sensor.setHumidity(50);
        sensor.setCalibrationTimestamp(LocalDateTime.of(2026, 5, 27, 16, 0));
        sensor.prePersist();
        sensor.setPlantCareSystem(system);
        sensor.setCreatedBy(creator);
        return sensor;
    }

    private static PlantCareSystem plantCareSystem(int id) {
        PlantCareSystem system = new PlantCareSystem();
        system.setId(id);
        system.setName("System " + id);
        system.setCreatedBy(user(99L, "owner"));
        return system;
    }

    private static User user(Long id, String username, String... roleNames) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        for (String roleName : roleNames) {
            user.addRole(new Role(roleName));
        }
        return user;
    }
}

package org.STPP.AgriAutomation.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.PlantService;
import org.STPP.AgriAutomation.api.services.SensorService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;
import org.STPP.AgriAutomation.data.entities.Plant;
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
class PlantControllerTest {

    @Mock
    private PlantService plantService;

    @Mock
    private SensorService sensorService;

    @Mock
    private PlantCareSystemService plantCareSystemService;

    @Mock
    private UserService userService;

    private PlantController controller;

    @BeforeEach
    void setUp() {
        controller = new PlantController(plantService, sensorService, plantCareSystemService, userService);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("alice", null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllReturnsPlantsForSensor() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor sensor = sensor(7, system);
        Plant first = plant(1, sensor, user(1L, "alice"));
        Plant second = plant(2, sensor, user(1L, "alice"));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(sensor));
        when(plantService.findAllBySensorId(7)).thenReturn(List.of(first, second));

        ResponseEntity<List<PlantResponseDTO>> response = controller.findAll(4, 7);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting(PlantResponseDTO::getId).containsExactly(1, 2);
    }

    @Test
    void findByIdRejectsPlantFromDifferentSensor() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor requestedSensor = sensor(7, system);
        Sensor otherSensor = sensor(8, system);
        Plant plant = plant(3, otherSensor, user(1L, "alice"));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(requestedSensor));
        when(plantService.findById(3)).thenReturn(Optional.of(plant));

        assertThatThrownBy(() -> controller.findById(4, 7, 3))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Plant with sensorId '7' not found");
    }

    @Test
    void createBuildsPlantForAuthenticatedUser() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor sensor = sensor(7, system);
        User creator = user(1L, "alice");
        Plant saved = plant(12, sensor, creator);
        saved.setName("Mint");
        saved.setGrowthStage("Seedling");
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(sensor));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));
        when(plantService.save(org.mockito.ArgumentMatchers.any(Plant.class))).thenReturn(saved);

        ResponseEntity<PlantResponseDTO> response =
                controller.create(4, 7, request("Mint", "Seedling"), UriComponentsBuilder.fromPath(""));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString())
                .isEqualTo("/plantcaresystems/4/sensors/7/plants/12");
        assertThat(response.getBody().getName()).isEqualTo("Mint");
    }

    @Test
    void updateByIdAllowsAdminAndSavesChangedFields() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor sensor = sensor(7, system);
        Plant existing = plant(12, sensor, user(1L, "alice"));
        User admin = user(2L, "admin", Role.ADMIN);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("admin", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(sensor));
        when(plantService.findById(12)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(plantService.save(existing)).thenReturn(existing);

        ResponseEntity<PlantResponseDTO> response =
                controller.updateById(4, 7, 12, request("Updated", "Flowering"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existing.getName()).isEqualTo("Updated");
        assertThat(existing.getGrowthStage()).isEqualTo("Flowering");
        verify(plantService).save(existing);
    }

    @Test
    void updateByIdReturnsForbiddenForNonCreatorNonAdmin() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor sensor = sensor(7, system);
        Plant existing = plant(12, sensor, user(1L, "alice"));
        User otherUser = user(2L, "bob");
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("bob", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(sensor));
        when(plantService.findById(12)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("bob")).thenReturn(Optional.of(otherUser));

        ResponseEntity<PlantResponseDTO> response =
                controller.updateById(4, 7, 12, request("Denied", "Fruiting"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(plantService, never()).save(existing);
    }

    @Test
    void deleteByIdAllowsCreatorAndDeletes() {
        PlantCareSystem system = plantCareSystem(4);
        Sensor sensor = sensor(7, system);
        User creator = user(1L, "alice");
        Plant existing = plant(12, sensor, creator);
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(system));
        when(sensorService.findById(7)).thenReturn(Optional.of(sensor));
        when(plantService.findById(12)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));

        ResponseEntity<Void> response = controller.deleteById(4, 7, 12);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(plantService).deleteById(12);
    }

    private static PlantRequestDTO request(String name, String growthStage) {
        return new PlantRequestDTO(name, growthStage, 7);
    }

    private static Plant plant(int id, Sensor sensor, User creator) {
        Plant plant = new Plant();
        plant.setId(id);
        plant.setName("Plant " + id);
        plant.setGrowthStage("Vegetative");
        plant.setSensor(sensor);
        plant.setCreatedBy(creator);
        return plant;
    }

    private static Sensor sensor(int id, PlantCareSystem system) {
        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setPlantCareSystem(system);
        sensor.setCreatedBy(user(99L, "sensor-owner"));
        return sensor;
    }

    private static PlantCareSystem plantCareSystem(int id) {
        PlantCareSystem system = new PlantCareSystem();
        system.setId(id);
        system.setCreatedBy(user(99L, "system-owner"));
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

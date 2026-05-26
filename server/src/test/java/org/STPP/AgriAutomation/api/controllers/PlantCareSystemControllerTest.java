package org.STPP.AgriAutomation.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.STPP.AgriAutomation.api.exceptions.ResourceNotFoundException;
import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemResponseDTO;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Role;
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

@ExtendWith(MockitoExtension.class)
class PlantCareSystemControllerTest {

    @Mock
    private PlantCareSystemService plantCareSystemService;

    @Mock
    private UserService userService;

    private PlantCareSystemController controller;

    @BeforeEach
    void setUp() {
        controller = new PlantCareSystemController(plantCareSystemService, userService);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("alice", null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateByIdAllowsCreatorAndSavesUpdatedFields() {
        User creator = user(1L, "alice");
        PlantCareSystem existing = plantCareSystem(4, "Old", "Old description", false, creator);
        PlantCareSystemRequestDTO request = request("New", "New description", true);
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));
        when(plantCareSystemService.save(existing)).thenReturn(existing);

        ResponseEntity<PlantCareSystemResponseDTO> response = controller.updateById(4, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("New description");
        assertThat(existing.isAutomationEnabled()).isTrue();
        assertThat(response.getBody().getName()).isEqualTo("New");
        verify(plantCareSystemService).save(existing);
    }

    @Test
    void updateByIdAllowsAdminEvenWhenNotCreator() {
        User creator = user(1L, "alice");
        User admin = user(2L, "admin", Role.ADMIN);
        PlantCareSystem existing = plantCareSystem(4, "Old", "Old description", false, creator);
        PlantCareSystemRequestDTO request = request("Admin update", "Updated by admin", true);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("admin", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(plantCareSystemService.save(existing)).thenReturn(existing);

        ResponseEntity<PlantCareSystemResponseDTO> response = controller.updateById(4, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existing.getName()).isEqualTo("Admin update");
        verify(plantCareSystemService).save(existing);
    }

    @Test
    void updateByIdReturnsForbiddenForNonCreatorNonAdmin() {
        User creator = user(1L, "alice");
        User otherUser = user(2L, "bob");
        PlantCareSystem existing = plantCareSystem(4, "Old", "Old description", false, creator);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("bob", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("bob")).thenReturn(Optional.of(otherUser));

        ResponseEntity<PlantCareSystemResponseDTO> response = controller.updateById(4, request("Nope", "Nope", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(plantCareSystemService, never()).save(existing);
    }

    @Test
    void deleteByIdAllowsCreatorAndDeletes() {
        User creator = user(1L, "alice");
        PlantCareSystem existing = plantCareSystem(4, "System", "Description", false, creator);
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(creator));

        ResponseEntity<Void> response = controller.deleteById(4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(plantCareSystemService).deleteById(4);
    }

    @Test
    void deleteByIdReturnsForbiddenForNonCreatorNonAdmin() {
        User creator = user(1L, "alice");
        User otherUser = user(2L, "bob");
        PlantCareSystem existing = plantCareSystem(4, "System", "Description", false, creator);
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("bob", null));
        when(plantCareSystemService.findById(4)).thenReturn(Optional.of(existing));
        when(userService.findByUsername("bob")).thenReturn(Optional.of(otherUser));

        ResponseEntity<Void> response = controller.deleteById(4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(plantCareSystemService, never()).deleteById(4);
    }

    @Test
    void findByIdThrowsResourceNotFoundWhenSystemMissing() {
        when(plantCareSystemService.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("PlantCareSystem with id '99' not found");
    }

    private static PlantCareSystemRequestDTO request(String name, String description, boolean automationEnabled) {
        return new PlantCareSystemRequestDTO(
                name,
                description,
                automationEnabled,
                LocalDateTime.of(2026, 5, 27, 16, 0));
    }

    private static PlantCareSystem plantCareSystem(
            int id,
            String name,
            String description,
            boolean automationEnabled,
            User createdBy) {
        PlantCareSystem system = new PlantCareSystem();
        system.setId(id);
        system.setName(name);
        system.setDescription(description);
        system.setAutomationEnabled(automationEnabled);
        system.setMaintenanceTimeStamp(LocalDateTime.of(2026, 5, 27, 15, 0));
        system.setCreatedBy(createdBy);
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

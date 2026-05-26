package org.STPP.AgriAutomation.data.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.Test;

class PlantCareSystemConverterTest {

    @Test
    void convertToResponseDTOCopiesFieldsAndCreator() {
        LocalDateTime maintenance = LocalDateTime.of(2026, 5, 27, 12, 0);
        PlantCareSystem system = plantCareSystem(8, "Greenhouse", "Main tunnel", true, maintenance, "alice");

        PlantCareSystemResponseDTO dto = PlantCareSystemConverter.convertToResponseDTO(system);

        assertThat(dto.getId()).isEqualTo(8);
        assertThat(dto.getName()).isEqualTo("Greenhouse");
        assertThat(dto.getDescription()).isEqualTo("Main tunnel");
        assertThat(dto.isAutomationEnabled()).isTrue();
        assertThat(dto.getMaintenanceTimeStamp()).isEqualTo(maintenance);
        assertThat(dto.getCreatedBy().getUsername()).isEqualTo("alice");
    }

    @Test
    void convertToEntityCopiesRequestFieldsAndCreator() {
        LocalDateTime maintenance = LocalDateTime.of(2026, 5, 27, 13, 30);
        PlantCareSystemRequestDTO request = new PlantCareSystemRequestDTO("Beds", "Raised beds", false, maintenance);
        User creator = user("bob");

        PlantCareSystem system = PlantCareSystemConverter.convertToEntity(request, creator);

        assertThat(system.getName()).isEqualTo("Beds");
        assertThat(system.getDescription()).isEqualTo("Raised beds");
        assertThat(system.isAutomationEnabled()).isFalse();
        assertThat(system.getMaintenanceTimeStamp()).isEqualTo(maintenance);
        assertThat(system.getCreatedBy()).isSameAs(creator);
    }

    @Test
    void convertToResponseDTOListMapsAllSystems() {
        LocalDateTime maintenance = LocalDateTime.of(2026, 5, 27, 14, 0);
        PlantCareSystem first = plantCareSystem(1, "North", "North area", true, maintenance, "chris");
        PlantCareSystem second = plantCareSystem(2, "South", "South area", false, maintenance, "dana");

        List<PlantCareSystemResponseDTO> dtos =
                PlantCareSystemConverter.convertToResponseDTOList(List.of(first, second));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(PlantCareSystemResponseDTO::getName).containsExactly("North", "South");
    }

    private static PlantCareSystem plantCareSystem(
            int id,
            String name,
            String description,
            boolean automationEnabled,
            LocalDateTime maintenance,
            String username) {
        PlantCareSystem system = new PlantCareSystem();
        system.setId(id);
        system.setName(name);
        system.setDescription(description);
        system.setAutomationEnabled(automationEnabled);
        system.setMaintenanceTimeStamp(maintenance);
        system.setCreatedBy(user(username));
        return system;
    }

    private static User user(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}

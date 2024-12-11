package org.STPP.AgriAutomation.data.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.User;

public class PlantCareSystemConverter {

    public static PlantCareSystemResponseDTO convertToResponseDTO(PlantCareSystem plantCareSystem) {
        UserDTO userDTO = new UserDTO(
                plantCareSystem.getCreatedBy().getUsername()
        );

        return new PlantCareSystemResponseDTO(
                plantCareSystem.getId(),
                plantCareSystem.getName(),
                plantCareSystem.getDescription(),
                plantCareSystem.isAutomationEnabled(),
                plantCareSystem.getMaintenanceTimeStamp(),
                userDTO
        );
    }

    public static List<PlantCareSystemResponseDTO> convertToResponseDTOList(List<PlantCareSystem> plantCareSystems) {
        return plantCareSystems.stream()
                .map(PlantCareSystemConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static PlantCareSystem convertToEntity(PlantCareSystemRequestDTO dto, User createdBy) {
        PlantCareSystem plantCareSystem = new PlantCareSystem();
        plantCareSystem.setName(dto.getName());
        plantCareSystem.setDescription(dto.getDescription());
        plantCareSystem.setAutomationEnabled(dto.isAutomationEnabled());

        if (dto.getMaintenanceTimeStamp() != null) {
            plantCareSystem.setMaintenanceTimeStamp(dto.getMaintenanceTimeStamp());
        }
        // Set the creator
        plantCareSystem.setCreatedBy(createdBy);

        // If maintenanceTimeStamp is null, it will be set by the @PrePersist method in the entity

        return plantCareSystem;
    }
}

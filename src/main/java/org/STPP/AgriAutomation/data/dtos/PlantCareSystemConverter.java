package org.STPP.AgriAutomation.data.dtos;

import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantCareSystemResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PlantCareSystemConverter {

    public static PlantCareSystemResponseDTO convertToResponseDTO(PlantCareSystem plantCareSystem) {
        return new PlantCareSystemResponseDTO(
                plantCareSystem.getId(),
                plantCareSystem.getName(),
                plantCareSystem.getDescription(),
                plantCareSystem.isAutomationEnabled(),
                plantCareSystem.getMaintenanceTimeStamp()
        );
    }

    public static List<PlantCareSystemResponseDTO> convertToResponseDTOList(List<PlantCareSystem> plantCareSystems) {
        return plantCareSystems.stream()
                .map(PlantCareSystemConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static PlantCareSystem convertToEntity(PlantCareSystemRequestDTO dto) {
        PlantCareSystem plantCareSystem = new PlantCareSystem();
        plantCareSystem.setName(dto.getName());
        plantCareSystem.setDescription(dto.getDescription());
        plantCareSystem.setAutomationEnabled(dto.isAutomationEnabled());
        plantCareSystem.setMaintenanceTimeStamp(dto.getMaintenanceTimeStamp());
        return plantCareSystem;
    }
}

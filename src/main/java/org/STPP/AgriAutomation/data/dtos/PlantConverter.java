package org.STPP.AgriAutomation.data.dtos;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.dtos.PlantRequestDTO;
import org.STPP.AgriAutomation.data.dtos.PlantResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PlantConverter {

    public static PlantResponseDTO convertToResponseDTO(Plant plant) {
        return new PlantResponseDTO(
                plant.getId(),
                plant.getName(),
                plant.getGrowthStage(),
                plant.getSensor().getId()
        );
    }

    public static List<PlantResponseDTO> convertToResponseDTOList(List<Plant> plants) {
        return plants.stream()
                .map(PlantConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static Plant convertToEntity(PlantRequestDTO dto) {
        Plant plant = new Plant();
        plant.setName(dto.getName());
        plant.setGrowthStage(dto.getGrowthStage());
        return plant;
    }
}

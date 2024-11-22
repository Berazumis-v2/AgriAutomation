package org.STPP.AgriAutomation.data.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;

public class PlantConverter {

    public static PlantResponseDTO convertToResponseDTO(Plant plant) {
        UserDTO userDTO = new UserDTO(
                plant.getCreatedBy().getId(),
                plant.getCreatedBy().getUsername()
        );

        return new PlantResponseDTO(
                plant.getId(),
                plant.getName(),
                plant.getGrowthStage(),
                plant.getSensor().getId(),
                userDTO
        );
    }

    public static List<PlantResponseDTO> convertToResponseDTOList(List<Plant> plants) {
        return plants.stream()
                .map(PlantConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public static Plant convertToEntity(PlantRequestDTO dto, Sensor sensor, User createdBy) {
        Plant plant = new Plant();
        plant.setName(dto.getName());
        plant.setGrowthStage(dto.getGrowthStage());
        plant.setSensor(sensor);
        plant.setCreatedBy(createdBy);
        return plant;
    }
}

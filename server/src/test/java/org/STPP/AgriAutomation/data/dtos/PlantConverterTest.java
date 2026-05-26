package org.STPP.AgriAutomation.data.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.Test;

class PlantConverterTest {

    @Test
    void convertToResponseDTOCopiesPlantFieldsAndRelationshipIds() {
        User creator = user("alice");
        Sensor sensor = new Sensor();
        sensor.setId(7);
        Plant plant = new Plant();
        plant.setId(3);
        plant.setName("Basil");
        plant.setGrowthStage("Vegetative");
        plant.setSensor(sensor);
        plant.setCreatedBy(creator);

        PlantResponseDTO dto = PlantConverter.convertToResponseDTO(plant);

        assertThat(dto.getId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("Basil");
        assertThat(dto.getGrowthStage()).isEqualTo("Vegetative");
        assertThat(dto.getSensorId()).isEqualTo(7);
        assertThat(dto.getCreatedBy().getUsername()).isEqualTo("alice");
    }

    @Test
    void convertToEntityCopiesRequestFieldsAndAssociations() {
        PlantRequestDTO request = new PlantRequestDTO("Mint", "Seedling", 9);
        Sensor sensor = new Sensor();
        User creator = user("bob");

        Plant plant = PlantConverter.convertToEntity(request, sensor, creator);

        assertThat(plant.getName()).isEqualTo("Mint");
        assertThat(plant.getGrowthStage()).isEqualTo("Seedling");
        assertThat(plant.getSensor()).isSameAs(sensor);
        assertThat(plant.getCreatedBy()).isSameAs(creator);
    }

    @Test
    void convertToResponseDTOListMapsAllPlants() {
        Plant first = plant(1, "Lettuce", "Seedling", 11, "chris");
        Plant second = plant(2, "Pepper", "Fruiting", 11, "dana");

        List<PlantResponseDTO> dtos = PlantConverter.convertToResponseDTOList(List.of(first, second));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(PlantResponseDTO::getName).containsExactly("Lettuce", "Pepper");
    }

    private static Plant plant(int id, String name, String stage, int sensorId, String username) {
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        Plant plant = new Plant();
        plant.setId(id);
        plant.setName(name);
        plant.setGrowthStage(stage);
        plant.setSensor(sensor);
        plant.setCreatedBy(user(username));
        return plant;
    }

    private static User user(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}

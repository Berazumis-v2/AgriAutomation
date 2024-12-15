package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PlantRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Growth stage is required")
    @Pattern(regexp = "^(Seedling|Vegetative|Flowering|Fruiting)$", 
            message = "Growth stage must be one of: Seedling, Vegetative, Flowering, Fruiting")
    private String growthStage;

    private int sensorId;

    public PlantRequestDTO() {}

    public PlantRequestDTO(String name, String growthStage, int sensorId) {
        this.name = name;
        this.growthStage = growthStage;
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(String growthStage) {
        this.growthStage = growthStage;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }
}

package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;

public class PlantRequestDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String growthStage;

    private int sensorId;  // Assuming we refer to Sensor by its ID

    public PlantRequestDTO() {}

    public PlantRequestDTO(String name, String growthStage, int sensorId) {
        this.name = name;
        this.growthStage = growthStage;
        this.sensorId = sensorId;
    }

    // Getters and Setters

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

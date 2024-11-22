package org.STPP.AgriAutomation.data.dtos;

public class PlantResponseDTO {

    private int id;
    private String name;
    private String growthStage;
    private int sensorId;

    private UserDTO createdBy;

    public PlantResponseDTO() {}

    public PlantResponseDTO(int id, String name, String growthStage, int sensorId, UserDTO createdBy) {
        this.id = id;
        this.name = name;
        this.growthStage = growthStage;
        this.sensorId = sensorId;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGrowthStage() {
        return growthStage;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGrowthStage(String growthStage) {
        this.growthStage = growthStage;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }
}

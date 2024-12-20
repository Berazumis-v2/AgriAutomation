package org.STPP.AgriAutomation.data.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PlantCareSystemResponseDTO {

    private int id;
    private String name;
    private String description;
    private boolean automationEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime maintenanceTimeStamp;

    private UserDTO createdBy;

    public PlantCareSystemResponseDTO() {}

    public PlantCareSystemResponseDTO(int id, String name, String description, boolean automationEnabled, LocalDateTime maintenanceTimeStamp, UserDTO createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.automationEnabled = automationEnabled;
        this.maintenanceTimeStamp = maintenanceTimeStamp;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutomationEnabled() {
        return automationEnabled;
    }

    public void setAutomationEnabled(boolean automationEnabled) {
        this.automationEnabled = automationEnabled;
    }

    public LocalDateTime getMaintenanceTimeStamp() {
        return maintenanceTimeStamp;
    }

    public void setMaintenanceTimeStamp(LocalDateTime maintenanceTimeStamp) {
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }

    public UserDTO getCreatedBy() {return createdBy;}

    public void setCreatedBy(UserDTO createdBy) {this.createdBy = createdBy;}
}

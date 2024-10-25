package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;
import java.sql.Timestamp;

public class PlantCareSystemRequestDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;
    private boolean automationEnabled;
    private Timestamp maintenanceTimeStamp;

    public PlantCareSystemRequestDTO() {}

    public PlantCareSystemRequestDTO(String name, String description, boolean automationEnabled, Timestamp maintenanceTimeStamp) {
        this.name = name;
        this.description = description;
        this.automationEnabled = automationEnabled;
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }

    // Getters and Setters

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

    public Timestamp getMaintenanceTimeStamp() {
        return maintenanceTimeStamp;
    }

    public void setMaintenanceTimeStamp(Timestamp maintenanceTimeStamp) {
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }
}

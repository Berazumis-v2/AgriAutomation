package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class PlantCareSystemRequestDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;
    private boolean automationEnabled;

    /**
     * maintenanceTimeStamp is optional during creation.
     * If not provided, it will be set to the current time by the system.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime maintenanceTimeStamp;

    public PlantCareSystemRequestDTO() {}

    public PlantCareSystemRequestDTO(String name, String description, boolean automationEnabled, LocalDateTime maintenanceTimeStamp) {
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

    public LocalDateTime getMaintenanceTimeStamp() {
        return maintenanceTimeStamp;
    }

    public void setMaintenanceTimeStamp(LocalDateTime maintenanceTimeStamp) {
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }
}

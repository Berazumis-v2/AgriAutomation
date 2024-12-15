package org.STPP.AgriAutomation.data.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlantCareSystemRequestDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;
    private boolean automationEnabled;

    @NotNull(message = "Maintenance timestamp is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime maintenanceTimeStamp;

    public PlantCareSystemRequestDTO() {}

    public PlantCareSystemRequestDTO(String name, String description, boolean automationEnabled, LocalDateTime maintenanceTimeStamp) {
        this.name = name;
        this.description = description;
        this.automationEnabled = automationEnabled;
        this.maintenanceTimeStamp = maintenanceTimeStamp;
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
}

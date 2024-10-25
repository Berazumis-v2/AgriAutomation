package org.STPP.AgriAutomation.data.dtos;

import java.sql.Timestamp;

public class PlantCareSystemResponseDTO {

    private int id;
    private String name;
    private String description;
    private boolean automationEnabled;
    private Timestamp maintenanceTimeStamp;

    public PlantCareSystemResponseDTO() {}

    public PlantCareSystemResponseDTO(int id, String name, String description, boolean automationEnabled, Timestamp maintenanceTimeStamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.automationEnabled = automationEnabled;
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }

    // Getters and Setters

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

    public Timestamp getMaintenanceTimeStamp() {
        return maintenanceTimeStamp;
    }

    public void setMaintenanceTimeStamp(Timestamp maintenanceTimeStamp) {
        this.maintenanceTimeStamp = maintenanceTimeStamp;
    }
}

package org.STPP.AgriAutomation.data.entities;

import jakarta.persistence.*;

import java.util.List;
import java.sql.Timestamp;

@Entity
public class PlantCareSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private boolean automationEnabled;
    private Timestamp MaintenanceTimeStamp;



    @OneToMany(mappedBy = "plantCareSystem",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors;

    public PlantCareSystem() {}

    public PlantCareSystem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public boolean isAutomationEnabled() {return automationEnabled;}

    public void setAutomationEnabled(boolean automationEnabled) {this.automationEnabled = automationEnabled;}

    public Timestamp getMaintenanceTimeStamp() {return MaintenanceTimeStamp;}

    public void setMaintenanceTimeStamp(Timestamp maintenanceTimeStamp) {this.MaintenanceTimeStamp = maintenanceTimeStamp;}

}

package org.STPP.AgriAutomation.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class PlantCareSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private boolean automationEnabled;
    private LocalDateTime maintenanceTimeStamp;

    @OneToMany(mappedBy = "plantCareSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    public PlantCareSystem() {}

    public PlantCareSystem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isAutomationEnabled() { return automationEnabled; }

    public void setAutomationEnabled(boolean automationEnabled) { this.automationEnabled = automationEnabled; }

    public LocalDateTime getMaintenanceTimeStamp() { return maintenanceTimeStamp; }

    public void setMaintenanceTimeStamp(LocalDateTime maintenanceTimeStamp) { this.maintenanceTimeStamp = maintenanceTimeStamp; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    @PrePersist
    protected void onCreate() {
        if (this.maintenanceTimeStamp == null) {
            this.maintenanceTimeStamp = LocalDateTime.now();
        }
    }
}

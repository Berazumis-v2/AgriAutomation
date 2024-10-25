package org.STPP.AgriAutomation.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String model;
    private int temperature;
    private int humidity;
    private LocalDateTime readingTimestamp;
    private LocalDateTime calibrationTimestamp;

    @ManyToOne
    @JoinColumn(name = "plantCareSystem_id", nullable = false)
    private PlantCareSystem plantCareSystem;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plant> plants;

    public Sensor() {}

    public Sensor(String model) {
        this.model = model;
    }

    public Sensor(String model, PlantCareSystem plantCareSystem) {
        this.model = model;
        this.plantCareSystem = plantCareSystem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public PlantCareSystem getPlantCareSystem() {
        return plantCareSystem;
    }

    public void setPlantCareSystem(PlantCareSystem plantCareSystem) {
        this.plantCareSystem = plantCareSystem;
    }

    public LocalDateTime getReadingTimestamp() {
        return readingTimestamp;
    }


    public LocalDateTime getCalibrationTimestamp() {
        return calibrationTimestamp;
    }


    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    // Lifecycle Callbacks to automatically set timestamps
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.readingTimestamp = now;
        this.calibrationTimestamp = now;
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        this.readingTimestamp = now;
        this.calibrationTimestamp = now;
    }
}


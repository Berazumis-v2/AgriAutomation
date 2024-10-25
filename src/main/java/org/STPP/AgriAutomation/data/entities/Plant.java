package org.STPP.AgriAutomation.data.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String growthStage; //e.g., Seedling, Vegetative, Flowering, etc

    // Association with Sensor
    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    public Plant() {}

    public Plant(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Sensor getSensor() {return sensor;}

    public void setSensor(Sensor sensor) {this.sensor = sensor;}

    public String getGrowthStage() {return growthStage;}

    public void setGrowthStage(String growthStage) {this.growthStage = growthStage;}
}

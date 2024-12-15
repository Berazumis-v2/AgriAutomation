package org.STPP.AgriAutomation.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Growth stage is required")
    @Pattern(regexp = "^(Seedling|Vegetative|Flowering|Fruiting)$", 
            message = "Growth stage must be one of: Seedling, Vegetative, Flowering, Fruiting")
    private String growthStage; //e.g., Seedling, Vegetative, Flowering, etc

    // Association with Sensor
    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

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

    public User getCreatedBy() {return createdBy;}

    public void setCreatedBy(User createdBy) {this.createdBy = createdBy;}
}

package org.STPP.AgriAutomation.data.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    // Association with Plant
    @ManyToMany
    @JoinTable(
        name = "sensor_plants",
        joinColumns = @JoinColumn(name = "sensor_id"),
        inverseJoinColumns = @JoinColumn(name = "plant_id")
    )
    private List<Plant> assignedPlants;

    public Sensor() {}

    public Sensor(int id, String name, List<Plant> assignedPlants) {
        this.id = id;
        this.name = name;
        this.assignedPlants = assignedPlants;
    }
}

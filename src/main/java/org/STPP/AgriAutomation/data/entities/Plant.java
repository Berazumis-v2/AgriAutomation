package org.STPP.AgriAutomation.data.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    // Association with Sensor
    @ManyToMany(mappedBy = "assignedPlants")
    private List<Sensor> sensors;

    // Constructors, getters, and setters
    public Plant() {}

    public Plant(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

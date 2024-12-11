package org.STPP.AgriAutomation.data.models;

import java.util.ArrayList;
import java.util.List;

public class Sensor {

    private int id;
    private String name;
    private List<Plant> assignedPlants;

    public Sensor() {
        this.assignedPlants = new ArrayList<>();
    }

    public Sensor(int id, String name, List<Plant> assignedPlants) {
        this.id = id;
        this.name = name;
        this.assignedPlants = assignedPlants != null ? assignedPlants : new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Plant> getAssignedPlants() { return assignedPlants; }
    public void setAssignedPlants(List<Plant> assignedPlants) {
        this.assignedPlants = assignedPlants != null ? assignedPlants : new ArrayList<>();
    }
}

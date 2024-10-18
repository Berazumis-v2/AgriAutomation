package org.STPP.AgriAutomation.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Plant {

    private int id;
    private String name;
    @JsonIgnore
    private Sensor sensor;

    public Plant() {} 

    public Plant(int id, String name, Sensor sensor) {
        this.id = id;
        this.name = name;
        this.sensor = sensor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
}

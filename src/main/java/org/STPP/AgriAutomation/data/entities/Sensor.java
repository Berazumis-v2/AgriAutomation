package org.STPP.AgriAutomation.data.entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String model;
    private int temparature;
    private int humidity;
    private Timestamp readingTimestamp;
    private Timestamp calibrationTimestamp;


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

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

    public int getTemparature() {return temparature;}

    public void setTemparature(int temparature) {this.temparature = temparature;}

    public int getHumidity() {return humidity;}

    public void setHumidity(int humidity) {this.humidity = humidity;}

    public PlantCareSystem getPlantCareSystem() {return plantCareSystem;}

    public void setPlantCareSystem(PlantCareSystem plantCareSystem) {this.plantCareSystem = plantCareSystem;}

    public Timestamp getReadingTimestamp() {return readingTimestamp;}

    public void setReadingTimestamp(Timestamp readingTimestamp) {this.readingTimestamp = readingTimestamp;}

    public Timestamp getCalibrationTimestamp() {return calibrationTimestamp;}

    public void setCalibrationTimestamp(Timestamp calibrationTimestamp) {this.calibrationTimestamp = calibrationTimestamp;}
}

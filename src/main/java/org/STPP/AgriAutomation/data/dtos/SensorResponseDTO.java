package org.STPP.AgriAutomation.data.dtos;

import java.sql.Timestamp;

public class SensorResponseDTO {

    private int id;
    private String model;
    private int temperature;
    private int humidity;
    private Timestamp readingTimestamp;
    private Timestamp calibrationTimestamp;
    private int plantCareSystemId;

    public SensorResponseDTO() {}

    public SensorResponseDTO(int id, String model, int temperature, int humidity, Timestamp readingTimestamp,
                             Timestamp calibrationTimestamp, int plantCareSystemId) {
        this.id = id;
        this.model = model;
        this.temperature = temperature;
        this.humidity = humidity;
        this.readingTimestamp = readingTimestamp;
        this.calibrationTimestamp = calibrationTimestamp;
        this.plantCareSystemId = plantCareSystemId;
    }

    // Getters and Setters

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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Timestamp getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(Timestamp readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public Timestamp getCalibrationTimestamp() {
        return calibrationTimestamp;
    }

    public void setCalibrationTimestamp(Timestamp calibrationTimestamp) {
        this.calibrationTimestamp = calibrationTimestamp;
    }

    public int getPlantCareSystemId() {
        return plantCareSystemId;
    }

    public void setPlantCareSystemId(int plantCareSystemId) {
        this.plantCareSystemId = plantCareSystemId;
    }
}

package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;
import java.sql.Timestamp;

public class SensorRequestDTO {

    @NotBlank(message = "Model is mandatory")
    private String model;

    private int temperature;
    private int humidity;
    private Timestamp readingTimestamp;
    private Timestamp calibrationTimestamp;
    private int plantCareSystemId;  // Assuming you refer to PlantCareSystem by its ID

    public SensorRequestDTO() {}

    public SensorRequestDTO(String model, int temperature, int humidity, Timestamp readingTimestamp,
                            Timestamp calibrationTimestamp, int plantCareSystemId) {
        this.model = model;
        this.temperature = temperature;
        this.humidity = humidity;
        this.readingTimestamp = readingTimestamp;
        this.calibrationTimestamp = calibrationTimestamp;
        this.plantCareSystemId = plantCareSystemId;
    }

    // Getters and Setters

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

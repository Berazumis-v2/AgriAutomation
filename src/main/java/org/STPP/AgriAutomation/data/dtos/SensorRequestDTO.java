package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SensorRequestDTO {

    @NotBlank(message = "Model is mandatory")
    private String model;

    private int temperature;
    private int humidity;

    /**
     * Optional fields. If not provided, they can be set to the current time or handled accordingly.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readingTimestamp;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime calibrationTimestamp;

    private int plantCareSystemId;  // Assuming you refer to PlantCareSystem by its ID

    public SensorRequestDTO() {}

    public SensorRequestDTO(String model, int temperature, int humidity, LocalDateTime readingTimestamp,
                            LocalDateTime calibrationTimestamp, int plantCareSystemId) {
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

    public LocalDateTime getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(LocalDateTime readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public LocalDateTime getCalibrationTimestamp() {
        return calibrationTimestamp;
    }

    public void setCalibrationTimestamp(LocalDateTime calibrationTimestamp) {
        this.calibrationTimestamp = calibrationTimestamp;
    }

    public int getPlantCareSystemId() {
        return plantCareSystemId;
    }

    public void setPlantCareSystemId(int plantCareSystemId) {
        this.plantCareSystemId = plantCareSystemId;
    }
}

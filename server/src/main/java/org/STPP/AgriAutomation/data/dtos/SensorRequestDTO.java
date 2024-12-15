package org.STPP.AgriAutomation.data.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SensorRequestDTO {

    @NotBlank(message = "Model is mandatory")
    private String model;

    private int temperature;
    private int humidity;

    @NotNull(message = "Calibration timestamp is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime calibrationTimestamp;

    public SensorRequestDTO() {}

    public SensorRequestDTO(String model, int temperature, int humidity, LocalDateTime calibrationTimestamp) {
        this.model = model;
        this.temperature = temperature;
        this.humidity = humidity;
        this.calibrationTimestamp = calibrationTimestamp;
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

    public LocalDateTime getCalibrationTimestamp() {
        return calibrationTimestamp;
    }

    public void setCalibrationTimestamp(LocalDateTime calibrationTimestamp) {
        this.calibrationTimestamp = calibrationTimestamp;
    }
}

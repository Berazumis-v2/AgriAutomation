package org.STPP.AgriAutomation.data.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SensorRequestDTO {

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;

    @Min(value = -50, message = "Temperature must be at least -50°C")
    @Max(value = 100, message = "Temperature must not exceed 100°C")
    private int temperature;

    @Min(value = 0, message = "Humidity must be at least 0%")
    @Max(value = 100, message = "Humidity must not exceed 100%")
    private int humidity;

    @NotNull(message = "Calibration timestamp is required")
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

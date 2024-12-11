package org.STPP.AgriAutomation.data.dtos;

import jakarta.validation.constraints.NotBlank;

public class SensorRequestDTO {

    @NotBlank(message = "Model is mandatory")
    private String model;

    private int temperature;
    private int humidity;

    public SensorRequestDTO() {}

    public SensorRequestDTO(String model, int temperature, int humidity) {
        this.model = model;
        this.temperature = temperature;
        this.humidity = humidity;
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
}

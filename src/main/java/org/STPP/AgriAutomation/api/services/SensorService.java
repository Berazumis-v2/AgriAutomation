package org.STPP.AgriAutomation.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.STPP.AgriAutomation.data.models.Plant;
import org.STPP.AgriAutomation.data.models.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SensorService {

    private final List<Sensor> sensorList = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(4);

    @Autowired
    private PlantService plantService;

    @PostConstruct
    public void initSensors() {
        Sensor sensor1 = new Sensor(1, "Temperature Sensor", new ArrayList<>());
        Sensor sensor2 = new Sensor(2, "Humidity Sensor", new ArrayList<>());
        Sensor sensor3 = new Sensor(3, "Soil Moisture Sensor", new ArrayList<>());
        Sensor sensor4 = new Sensor(4, "Light Sensor", new ArrayList<>());

        sensorList.addAll(Arrays.asList(sensor1, sensor2, sensor3, sensor4));

        Plant plant1 = new Plant(1,"Rose", null);
        Plant plant2 = new Plant(2,"Dracaena trifasciata", null);
        Plant plant3 = new Plant(3,"Jade plant", null);
        Plant plant4 = new Plant(4,"Swiss cheese plant", null);
        Plant plant5 = new Plant(5,"Golden Pothos", null);

        assignPlantToSensor(sensor1.getId(), plant1);
        assignPlantToSensor(sensor1.getId(), plant2);
        assignPlantToSensor(sensor2.getId(), plant3);
        assignPlantToSensor(sensor3.getId(), plant4);
        assignPlantToSensor(sensor4.getId(), plant5);
    }

    public Sensor getSensor(Integer id){
        return sensorList.stream()
                .filter(sensor -> sensor.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean sensorExists(Integer id){
        return sensorList.stream().anyMatch(sensor -> sensor.getId() == id);
    }

    public List<Sensor> getAllSensors(){
        return new ArrayList<>(sensorList);
    }

    public Sensor addSensor(Sensor sensor){
        sensor.setId(idGenerator.incrementAndGet());
        sensorList.add(sensor);
        return sensor;
    }

    public Sensor updateSensor(Sensor updatedSensor) {
        Sensor existingSensor = getSensor(updatedSensor.getId());
        if (existingSensor != null) {
            existingSensor.setName(updatedSensor.getName());
            if (updatedSensor.getAssignedPlants() != null) {
                existingSensor.setAssignedPlants(updatedSensor.getAssignedPlants());
            }
        }
        return existingSensor;
    }

    public void removeSensor(Integer id) {
        Sensor sensor = getSensor(id);
        if (sensor != null) {
            List<Plant> assignedPlants = new ArrayList<>(sensor.getAssignedPlants());
            for (Plant plant : assignedPlants) {
                removePlantFromSensor(id, plant.getId());
                plantService.removePlant(plant);
            }
            sensorList.remove(sensor);
        }
    }

    public void assignPlantToSensor(Integer sensorId, Plant plant) {
        Sensor sensor = getSensor(sensorId);
        if (sensor != null && plant != null) {
            if (plant.getSensor() != null) {
                removePlantFromSensor(plant.getSensor().getId(), plant.getId());
            }
            plant.setSensor(sensor);
            sensor.getAssignedPlants().add(plant);
        }
    }

    public void removePlantFromSensor(Integer sensorId, Integer plantId) {
        Sensor sensor = getSensor(sensorId);
        if (sensor != null) {
            Plant plantToRemove = sensor.getAssignedPlants().stream()
                .filter(plant -> plant.getId() == plantId)
                .findFirst()
                .orElse(null);

            if (plantToRemove != null) {
                plantToRemove.setSensor(null);
                sensor.getAssignedPlants().remove(plantToRemove);
            }
        }
    }
}

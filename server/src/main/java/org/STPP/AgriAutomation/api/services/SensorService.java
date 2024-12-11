package org.STPP.AgriAutomation.api.services;

import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.repositories.SensorRepository;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Iterable<Sensor> findAll()
    {
       return sensorRepository.findAll();
    }

    public Iterable<Sensor> findAllByPlantCareSystemId(int plantCareSystemId) {
        return sensorRepository.findAllByPlantCareSystemId(plantCareSystemId);
    }

    public Optional<Sensor> findById(int id) {
        return sensorRepository.findById(id);
    }

    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public void deleteById(int id) {
        sensorRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return sensorRepository.existsById(id);
    }

}

package org.STPP.AgriAutomation.api.repositories;

import org.STPP.AgriAutomation.data.entities.Sensor;
import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<Sensor, Integer> {
    Iterable<Sensor> findAllByPlantCareSystemId(int PlantId);
}

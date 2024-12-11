package org.STPP.AgriAutomation.api.repositories;

import org.STPP.AgriAutomation.data.entities.Plant;
import org.springframework.data.repository.CrudRepository;

public interface PlantRepository extends CrudRepository<Plant, Integer> {
    Iterable<Plant> findAllBySensorId(int sensorId);
}

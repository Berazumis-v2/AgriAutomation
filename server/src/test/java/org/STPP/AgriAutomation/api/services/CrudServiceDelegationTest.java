package org.STPP.AgriAutomation.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.repositories.PCSRepository;
import org.STPP.AgriAutomation.api.repositories.PlantRepository;
import org.STPP.AgriAutomation.api.repositories.SensorRepository;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrudServiceDelegationTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private PCSRepository pcsRepository;

    @Test
    void plantServiceDelegatesRepositoryOperations() {
        PlantService service = new PlantService(plantRepository);
        Plant plant = new Plant();
        when(plantRepository.findAll()).thenReturn(List.of(plant));
        when(plantRepository.findById(3)).thenReturn(Optional.of(plant));
        when(plantRepository.findAllBySensorId(7)).thenReturn(List.of(plant));
        when(plantRepository.save(plant)).thenReturn(plant);
        when(plantRepository.existsById(3)).thenReturn(true);

        assertThat(service.findAll()).containsExactly(plant);
        assertThat(service.findById(3)).contains(plant);
        assertThat(service.findAllBySensorId(7)).containsExactly(plant);
        assertThat(service.save(plant)).isSameAs(plant);
        assertThat(service.existsById(3)).isTrue();
        service.deleteById(3);

        verify(plantRepository).deleteById(3);
    }

    @Test
    void sensorServiceDelegatesRepositoryOperations() {
        SensorService service = new SensorService(sensorRepository);
        Sensor sensor = new Sensor();
        when(sensorRepository.findAll()).thenReturn(List.of(sensor));
        when(sensorRepository.findById(3)).thenReturn(Optional.of(sensor));
        when(sensorRepository.findAllByPlantCareSystemId(7)).thenReturn(List.of(sensor));
        when(sensorRepository.save(sensor)).thenReturn(sensor);
        when(sensorRepository.existsById(3)).thenReturn(true);

        assertThat(service.findAll()).containsExactly(sensor);
        assertThat(service.findById(3)).contains(sensor);
        assertThat(service.findAllByPlantCareSystemId(7)).containsExactly(sensor);
        assertThat(service.save(sensor)).isSameAs(sensor);
        assertThat(service.existsById(3)).isTrue();
        service.deleteById(3);

        verify(sensorRepository).deleteById(3);
    }

    @Test
    void plantCareSystemServiceDelegatesRepositoryOperations() {
        PlantCareSystemService service = new PlantCareSystemService(pcsRepository);
        PlantCareSystem system = new PlantCareSystem();
        when(pcsRepository.findAll()).thenReturn(List.of(system));
        when(pcsRepository.findById(3)).thenReturn(Optional.of(system));
        when(pcsRepository.save(system)).thenReturn(system);
        when(pcsRepository.existsById(3)).thenReturn(true);

        assertThat(service.findAll()).containsExactly(system);
        assertThat(service.findById(3)).contains(system);
        assertThat(service.save(system)).isSameAs(system);
        assertThat(service.existsById(3)).isTrue();
        service.deleteById(3);

        verify(pcsRepository).deleteById(3);
    }
}

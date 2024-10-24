package org.STPP.AgriAutomation.data.seed;

import org.STPP.AgriAutomation.api.repositories.PCSRepository;
import org.STPP.AgriAutomation.api.repositories.PlantRepository;
import org.STPP.AgriAutomation.api.repositories.SensorRepository;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PCSRepository pcsRepository;
    private final SensorRepository sensorRepository;
    private final PlantRepository plantRepository;

    public DataSeeder(PCSRepository pcsRepository, SensorRepository sensorRepository, PlantRepository plantRepository) {
        this.pcsRepository = pcsRepository;
        this.sensorRepository = sensorRepository;
        this.plantRepository = plantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists to avoid duplication
        if (pcsRepository.count() == 0) {
            // Create PlantCareSystems
            PlantCareSystem greenhouseSystem = new PlantCareSystem("Greenhouse System", "Automated greenhouse control system");
            greenhouseSystem.setAutomationEnabled(true);
            greenhouseSystem.setMaintenanceTimeStamp(Timestamp.from(Instant.parse("2024-04-01T10:00:00Z")));

            PlantCareSystem outdoorSystem = new PlantCareSystem("Outdoor System", "Outdoor plant monitoring system");
            outdoorSystem.setAutomationEnabled(false);
            outdoorSystem.setMaintenanceTimeStamp(Timestamp.from(Instant.parse("2024-03-15T09:30:00Z")));

            PlantCareSystem verticalFarmSystem = new PlantCareSystem("Vertical Farm System", "Space-efficient vertical farming system");
            verticalFarmSystem.setAutomationEnabled(true);
            verticalFarmSystem.setMaintenanceTimeStamp(Timestamp.from(Instant.parse("2024-05-10T08:45:00Z")));

            pcsRepository.saveAll(List.of(greenhouseSystem, outdoorSystem, verticalFarmSystem));

            // Create Sensors for Greenhouse System
            Sensor tempSensorGH = new Sensor("TempModelGH-100", greenhouseSystem);
            tempSensorGH.setTemparature(22);
            tempSensorGH.setHumidity(55);
            tempSensorGH.setReadingTimestamp(Timestamp.from(Instant.now()));
            tempSensorGH.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-04-01T09:00:00Z")));

            Sensor humiditySensorGH = new Sensor("HumModelGH-200", greenhouseSystem);
            humiditySensorGH.setTemparature(23);
            humiditySensorGH.setHumidity(60);
            humiditySensorGH.setReadingTimestamp(Timestamp.from(Instant.now()));
            humiditySensorGH.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-04-01T09:15:00Z")));

            Sensor lightSensorGH = new Sensor("LightModelGH-300", greenhouseSystem);
            lightSensorGH.setTemparature(21);
            lightSensorGH.setHumidity(58);
            lightSensorGH.setReadingTimestamp(Timestamp.from(Instant.now()));
            lightSensorGH.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-04-01T09:30:00Z")));

            // Create Sensors for Outdoor System
            Sensor tempSensorOD = new Sensor("TempModelOD-100", outdoorSystem);
            tempSensorOD.setTemparature(18);
            tempSensorOD.setHumidity(40);
            tempSensorOD.setReadingTimestamp(Timestamp.from(Instant.now()));
            tempSensorOD.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-03-15T08:00:00Z")));

            Sensor soilMoistureSensorOD = new Sensor("SoilModelOD-200", outdoorSystem);
            soilMoistureSensorOD.setTemparature(19);
            soilMoistureSensorOD.setHumidity(45);
            soilMoistureSensorOD.setReadingTimestamp(Timestamp.from(Instant.now()));
            soilMoistureSensorOD.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-03-15T08:15:00Z")));

            // Create Sensors for Vertical Farm System
            Sensor tempSensorVF = new Sensor("TempModelVF-100", verticalFarmSystem);
            tempSensorVF.setTemparature(20);
            tempSensorVF.setHumidity(50);
            tempSensorVF.setReadingTimestamp(Timestamp.from(Instant.now()));
            tempSensorVF.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-05-10T07:00:00Z")));

            Sensor humiditySensorVF = new Sensor("HumModelVF-200", verticalFarmSystem);
            humiditySensorVF.setTemparature(21);
            humiditySensorVF.setHumidity(55);
            humiditySensorVF.setReadingTimestamp(Timestamp.from(Instant.now()));
            humiditySensorVF.setCalibrationTimestamp(Timestamp.from(Instant.parse("2024-05-10T07:15:00Z")));

            sensorRepository.saveAll(List.of(
                    tempSensorGH, humiditySensorGH, lightSensorGH,
                    tempSensorOD, soilMoistureSensorOD,
                    tempSensorVF, humiditySensorVF
            ));

            // Create Plants for Greenhouse System
            Plant tomatoPlant = new Plant();
            tomatoPlant.setName("Tomato");
            tomatoPlant.setGrowthStage("Vegetative");
            tomatoPlant.setSensor(tempSensorGH);

            Plant lettucePlant = new Plant();
            lettucePlant.setName("Lettuce");
            lettucePlant.setGrowthStage("Seedling");
            lettucePlant.setSensor(humiditySensorGH);

            Plant basilPlant = new Plant();
            basilPlant.setName("Basil");
            basilPlant.setGrowthStage("Flowering");
            basilPlant.setSensor(lightSensorGH);

            // Create Plants for Outdoor System
            Plant pumpkinPlant = new Plant();
            pumpkinPlant.setName("Pumpkin");
            pumpkinPlant.setGrowthStage("Fruit Development");
            pumpkinPlant.setSensor(tempSensorOD);

            Plant carrotPlant = new Plant();
            carrotPlant.setName("Carrot");
            carrotPlant.setGrowthStage("Root Development");
            carrotPlant.setSensor(soilMoistureSensorOD);

            // Create Plants for Vertical Farm System
            Plant spinachPlant = new Plant();
            spinachPlant.setName("Spinach");
            spinachPlant.setGrowthStage("Mature");
            spinachPlant.setSensor(tempSensorVF);

            Plant kalePlant = new Plant();
            kalePlant.setName("Kale");
            kalePlant.setGrowthStage("Vegetative");
            kalePlant.setSensor(humiditySensorVF);

            plantRepository.saveAll(List.of(
                    tomatoPlant, lettucePlant, basilPlant,
                    pumpkinPlant, carrotPlant,
                    spinachPlant, kalePlant
            ));

            System.out.println("Sample data seeded successfully.");
        } else {
            System.out.println("Data already exists. Skipping seeding.");
        }
    }
}

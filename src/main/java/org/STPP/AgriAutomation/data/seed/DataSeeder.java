package org.STPP.AgriAutomation.data.seed;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.STPP.AgriAutomation.api.repositories.PCSRepository;
import org.STPP.AgriAutomation.api.repositories.PlantRepository;
import org.STPP.AgriAutomation.api.repositories.RoleRepository;
import org.STPP.AgriAutomation.api.repositories.SensorRepository;
import org.STPP.AgriAutomation.api.repositories.UserRepo;
import org.STPP.AgriAutomation.data.entities.Plant;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.Sensor;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PCSRepository pcsRepository;
    private final SensorRepository sensorRepository;
    private final PlantRepository plantRepository;
    private final RoleRepository roleRepository;
    private final UserRepo userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(PCSRepository pcsRepository,
                      SensorRepository sensorRepository,
                      PlantRepository plantRepository,
                      RoleRepository roleRepository,
                      UserRepo userRepository) {
        this.pcsRepository = pcsRepository;
        this.sensorRepository = sensorRepository;
        this.plantRepository = plantRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public void run(String... args) throws Exception {
        seedPlantCareSystemsAndSensors();
        seedRoles();
        seedUsers();
    }

    /**
     * Seeds PlantCareSystems, Sensors, and Plants.
     */
    private void seedPlantCareSystemsAndSensors() {
        // Check if PlantCareSystems already exist
        if (pcsRepository.count() == 0) {
            // Create PlantCareSystems
            PlantCareSystem greenhouseSystem = new PlantCareSystem("Greenhouse System", "Automated greenhouse control system");
            greenhouseSystem.setAutomationEnabled(true);
            greenhouseSystem.setMaintenanceTimeStamp(LocalDateTime.parse("2024-04-01T10:00:00"));

            PlantCareSystem outdoorSystem = new PlantCareSystem("Outdoor System", "Outdoor plant monitoring system");
            outdoorSystem.setAutomationEnabled(false);
            outdoorSystem.setMaintenanceTimeStamp(LocalDateTime.parse("2024-03-15T09:30:00"));

            PlantCareSystem verticalFarmSystem = new PlantCareSystem("Vertical Farm System", "Space-efficient vertical farming system");
            verticalFarmSystem.setAutomationEnabled(true);
            verticalFarmSystem.setMaintenanceTimeStamp(LocalDateTime.parse("2024-05-10T08:45:00"));

            pcsRepository.saveAll(List.of(greenhouseSystem, outdoorSystem, verticalFarmSystem));

            // Create Sensors for Greenhouse System
            Sensor tempSensorGH = new Sensor("TempModelGH-100", greenhouseSystem);
            tempSensorGH.setTemperature(22);
            tempSensorGH.setHumidity(55);

            Sensor humiditySensorGH = new Sensor("HumModelGH-200", greenhouseSystem);
            humiditySensorGH.setTemperature(23);
            humiditySensorGH.setHumidity(60);

            Sensor lightSensorGH = new Sensor("LightModelGH-300", greenhouseSystem);
            lightSensorGH.setTemperature(21);
            lightSensorGH.setHumidity(58);

            // Create Sensors for Outdoor System
            Sensor tempSensorOD = new Sensor("TempModelOD-100", outdoorSystem);
            tempSensorOD.setTemperature(18);
            tempSensorOD.setHumidity(40);

            Sensor soilMoistureSensorOD = new Sensor("SoilModelOD-200", outdoorSystem);
            soilMoistureSensorOD.setTemperature(19);
            soilMoistureSensorOD.setHumidity(45);

            // Create Sensors for Vertical Farm System
            Sensor tempSensorVF = new Sensor("TempModelVF-100", verticalFarmSystem);
            tempSensorVF.setTemperature(20);
            tempSensorVF.setHumidity(50);

            Sensor humiditySensorVF = new Sensor("HumModelVF-200", verticalFarmSystem);
            humiditySensorVF.setTemperature(21);
            humiditySensorVF.setHumidity(55);

            sensorRepository.saveAll(Arrays.asList(
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

            plantRepository.saveAll(Arrays.asList(
                    tomatoPlant, lettucePlant, basilPlant,
                    pumpkinPlant, carrotPlant,
                    spinachPlant, kalePlant
            ));

            System.out.println("PlantCareSystems, Sensors, and Plants seeded successfully.");
        } else {
            System.out.println("PlantCareSystems already exist. Skipping seeding for systems, sensors, and plants.");
        }
    }

    /**
     * Seeds roles into the database.
     */
    private void seedRoles() {
        List<String> roles = Arrays.asList("USER", "ADMIN");

        for (String roleName : roles) {
            Optional<Role> roleOpt = roleRepository.findByName(roleName);
            if (!roleOpt.isPresent()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                System.out.printf("Role '%s' created.%n", roleName);
            } else {
                System.out.printf("Role '{}' already exists. Skipping creation.", roleName);
            }
        }
    }

    

    /**
     * Seeds two users: one simple user and one admin.
     */
    private void seedUsers() {
        // Create Simple User
        String simpleUsername = "simpleUser";
        String simplePassword = "password123"; // In production, use a more secure password
        Optional<User> existingSimpleUser = userRepository.findByUsername(simpleUsername);

        if (!existingSimpleUser.isPresent()) {
            User simpleUser = new User();
            simpleUser.setUsername(simpleUsername);
            simpleUser.setPassword(passwordEncoder.encode(simplePassword));

            // Assign the USER role
            Optional<Role> userRoleOpt = roleRepository.findByName("USER");
            if (userRoleOpt.isPresent()) {
                simpleUser.setRoles(new HashSet<>(List.of(userRoleOpt.get())));
            } else {
                System.out.printf("USER role not found. Cannot assign role to simple user.");
                return; // Exit if USER role is not present
            }

            userRepository.save(simpleUser);
            System.out.printf("Simple user '{}' created.", simpleUsername);
        } else {
            System.out.printf("Simple user '{}' already exists. Skipping creation.", simpleUsername);
        }

        // Create Admin User
        String adminUsername = "adminUser";
        String adminPassword = "adminPass123"; // In production, use a more secure password
        Optional<User> existingAdminUser =userRepository.findByUsername(adminUsername);

        if (!existingAdminUser.isPresent()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));

            // Assign the ADMIN role
            Optional<Role> adminRoleOpt = roleRepository.findByName("ADMIN");
            if (adminRoleOpt.isPresent()) {
                adminUser.setRoles(new HashSet<>(List.of(adminRoleOpt.get())));
            } else {
                System.out.printf("ADMIN role not found. Cannot assign role to admin user.");
                return; // Exit if ADMIN role is not present
            }

            userRepository.save(adminUser);
            System.out.printf("Admin user '{}' created.", adminUsername);
        } else {
            System.out.printf("Admin user '{}' already exists. Skipping creation.", adminUsername);
        }
    }
}
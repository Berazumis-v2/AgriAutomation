package org.STPP.AgriAutomation.data.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class DtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void plantRequestAcceptsValidGrowthStage() {
        PlantRequestDTO dto = new PlantRequestDTO("Tomato", "Flowering", 4);

        Set<ConstraintViolation<PlantRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void plantRequestRejectsInvalidGrowthStageAndBlankName() {
        PlantRequestDTO dto = new PlantRequestDTO("", "Dormant", 4);

        Set<ConstraintViolation<PlantRequestDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name", "growthStage");
    }

    @Test
    void sensorRequestRejectsOutOfRangeTemperatureHumidityAndMissingCalibration() {
        SensorRequestDTO dto = new SensorRequestDTO("S1", -51, 101, null);

        Set<ConstraintViolation<SensorRequestDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("temperature", "humidity", "calibrationTimestamp");
    }

    @Test
    void sensorRequestAcceptsBoundaryValues() {
        LocalDateTime calibration = LocalDateTime.of(2026, 5, 27, 10, 30);
        SensorRequestDTO dto = new SensorRequestDTO("Greenhouse sensor", -50, 100, calibration);

        Set<ConstraintViolation<SensorRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void plantCareSystemRequestRequiresNameAndMaintenanceTimestamp() {
        PlantCareSystemRequestDTO dto = new PlantCareSystemRequestDTO("", "Hydroponics", true, null);

        Set<ConstraintViolation<PlantCareSystemRequestDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "maintenanceTimeStamp");
    }
}

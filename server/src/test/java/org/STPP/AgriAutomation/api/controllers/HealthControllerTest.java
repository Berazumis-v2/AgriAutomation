package org.STPP.AgriAutomation.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthControllerTest {

    @Test
    void healthCheckReturnsStatusAndTimestamp() {
        HealthController controller = new HealthController();

        ResponseEntity<Object> response = controller.healthCheck();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("status")).isEqualTo("Connected successfully to AgriAutomation backend!");
        assertThat(body.get("timestamp")).isInstanceOf(String.class);
    }
}

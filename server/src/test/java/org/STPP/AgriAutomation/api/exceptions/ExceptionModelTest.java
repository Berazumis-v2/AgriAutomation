package org.STPP.AgriAutomation.api.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ExceptionModelTest {

    @Test
    void resourceNotFoundExceptionFormatsMessageAndExposesFields() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Plant", "id", 42);

        assertThat(exception.getMessage()).isEqualTo("Plant with id '42' not found");
        assertThat(exception.getResource()).isEqualTo("Plant");
        assertThat(exception.getField()).isEqualTo("id");
        assertThat(exception.getValue()).isEqualTo(42);
    }

    @Test
    void errorResponseAndDetailExposeMutableProperties() {
        ErrorDetail detail = new ErrorDetail("Plant", "name", "NotBlank");
        ErrorResponse response = new ErrorResponse("Validation Failed", List.of(detail));

        assertThat(response.getMessage()).isEqualTo("Validation Failed");
        assertThat(response.getErrors()).containsExactly(detail);
        assertThat(detail.getResource()).isEqualTo("Plant");
        assertThat(detail.getField()).isEqualTo("name");
        assertThat(detail.getCode()).isEqualTo("NotBlank");
    }

    @Test
    void globalExceptionHandlerConvertsResourceNotFoundTo404Response() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Sensor", "id", 9));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Sensor with id '9' not found");
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getCode()).isEqualTo("not_found");
    }
}

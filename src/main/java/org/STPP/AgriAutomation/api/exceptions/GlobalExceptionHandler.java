// GlobalExceptionHandler.java
package org.STPP.AgriAutomation.api.exceptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        ErrorDetail errorDetail = new ErrorDetail("Request", "body", "unreadable");

        ErrorResponse errorResponse = new ErrorResponse(
                "Required request body is missing or unreadable",
                Arrays.asList(errorDetail)
        );

        logger.info("Http message not readable error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDetail errorDetail = new ErrorDetail(
                ex.getResource(),
                ex.getField(),
                "not_found"
        );

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), Arrays.asList(errorDetail));

        logger.info("Resource not found error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String errorMessage = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type is '%s'.",
                ex.getValue(), ex.getName(), expectedType
        );

        ErrorDetail errorDetail = new ErrorDetail("Parameter", ex.getName(), "type_mismatch");

        ErrorResponse errorResponse = new ErrorResponse(errorMessage, Arrays.asList(errorDetail));

        logger.info("Type mismatch error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetail(
                        error.getObjectName(), // resource
                        error.getField(),      // field
                        error.getCode()))      // code
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", errorDetails);

        logger.info("Validation error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(TransactionSystemException ex) {
        ErrorDetail errorDetail = new ErrorDetail("Transaction", "", "invalid_input");

        ErrorResponse errorResponse = new ErrorResponse("Invalid input", Arrays.asList(errorDetail));

        logger.info("Transaction system error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorDetail errorDetail = new ErrorDetail(
                "Endpoint",
                ex.getRequestURL(),
                "not_found"
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid URL used: " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                Arrays.asList(errorDetail)
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String supportedMethods = String.join(", ", ex.getSupportedMethods());

        ErrorDetail errorDetail = new ErrorDetail(
                "Method",
                ex.getMethod(),
                "method_not_allowed"
        );

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMethod() + " method is not supported for this endpoint. Supported methods are: " + supportedMethods,
                Arrays.asList(errorDetail)
        );

        logger.info("Method not supported error: {}", errorResponse);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
}

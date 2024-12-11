// ResourceNotFoundException.java
package org.STPP.AgriAutomation.api.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String resource;
    private String field;
    private Object value;

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s with %s '%s' not found", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    // Getters
    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}

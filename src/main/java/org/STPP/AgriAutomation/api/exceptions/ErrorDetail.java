// ErrorDetail.java
package org.STPP.AgriAutomation.api.exceptions;

public class ErrorDetail {
    private String resource;
    private String field;
    private String code;

    public ErrorDetail() {}

    public ErrorDetail(String resource, String field, String code) {
        this.resource = resource;
        this.field = field;
        this.code = code;
    }

    // Getters and Setters
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}

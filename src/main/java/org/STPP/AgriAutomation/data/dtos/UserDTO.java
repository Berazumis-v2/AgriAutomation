package org.STPP.AgriAutomation.data.dtos;

public class UserDTO {
    private String username;

    public UserDTO() {}

    public UserDTO( String username) {
        this.username = username;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package org.STPP.AgriAutomation.data.dtos.auth;

import org.STPP.AgriAutomation.data.entities.User;

public class RegisterRequest {
    private String username;

    private String password;

    public User toEntity() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

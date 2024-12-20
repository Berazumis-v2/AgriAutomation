package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.UserDTO;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class UserController {

    @Autowired
    private UserService service;


    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Validated @RequestBody RegisterRequest registerRequest) {
        User user = service.register(registerRequest);
        UserDTO userDTO = new UserDTO(user.getUsername());
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = service.login(loginRequest);

        // Create HttpOnly cookie for refresh token with proper security settings
        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)  // Changed to true for production
                .path("/")
                .maxAge(3 * 24 * 60 * 60)
                .sameSite("Strict")  // Changed to Strict for production
                .domain(null)  // Let the browser set the appropriate domain
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new AuthResponse(authResponse.getAccessToken(), "Cookie set"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            AuthResponse authResponse = service.refreshAccessToken(refreshToken);
            
            ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)  // Changed to true for production
                    .path("/")
                    .maxAge(3 * 24 * 60 * 60)
                    .sameSite("Strict")  // Changed to Strict for production
                    .domain(null)  // Let the browser set the appropriate domain
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            service.logout(refreshToken);
        }
        
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)  // Changed to true for production
                .path("/")
                .maxAge(0)
                .sameSite("Strict")  // Changed to Strict for production
                .domain(null)  // Let the browser set the appropriate domain
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("Logged out successfully");
    }

    @GetMapping("/check-token")
    public ResponseEntity<Boolean> checkRefreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                // Only validate the token without refreshing it
                service.validateRefreshToken(refreshToken);
                return ResponseEntity.ok(true);
            } catch (Exception e) {
                return ResponseEntity.ok(false);
            }
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            service.validateAccessToken(token);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

package org.STPP.AgriAutomation.api.services;

import java.util.Optional;

import org.STPP.AgriAutomation.api.repositories.RoleRepository;
import org.STPP.AgriAutomation.api.repositories.UserRepo;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.RefreshToken;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public User register(RegisterRequest registerRequest) {
        if (userRepo.findByUsername(registerRequest.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Assign default role, e.g., "USER"
        Role userRole = new Role("ROLE_USER");
        user.getRoles().add(userRole);

        return userRepo.save(user);
    }


    // public String verify(LoginRequest loginRequest) {
    //     try {
    //         Authentication authentication = authManager.authenticate(
    //                 new UsernamePasswordAuthenticationToken(
    //                         loginRequest.getUsername(),
    //                         loginRequest.getPassword()
    //                 )
    //         );

    //         if (authentication.isAuthenticated()) {
    //             User user = (User) authentication.getPrincipal();
    //             return jwtService.generateToken(user);
    //         }
    //     } catch (AuthenticationException e) {
    //         // Log authentication failure
    //         logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
    //     }
    //     return "fail";
    // }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new AuthResponse(accessToken, refreshToken.getToken());
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(refreshToken);

        if (!optionalRefreshToken.isPresent()) {
            throw new RuntimeException("Refresh token not found");
        }

        RefreshToken token = refreshTokenService.verifyExpiration(optionalRefreshToken.get());

        User user = token.getUser();
        return jwtService.generateAccessToken(user);
    }

    public String rotateRefreshToken(String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(refreshToken);

        if (!optionalRefreshToken.isPresent()) {
            throw new RuntimeException("Refresh token not found");
        }

        RefreshToken token = optionalRefreshToken.get();

        // Revoke the old refresh token
        refreshTokenService.revokeToken(token);

        // Create a new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser());

        return newRefreshToken.getToken();
    }

    public void logout(String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(refreshToken);

        if (optionalRefreshToken.isPresent()) {
            refreshTokenService.revokeToken(optionalRefreshToken.get());
        }
    }
}

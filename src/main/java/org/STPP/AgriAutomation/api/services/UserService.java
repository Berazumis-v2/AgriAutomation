package org.STPP.AgriAutomation.api.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.STPP.AgriAutomation.api.repositories.RoleRepository;
import org.STPP.AgriAutomation.api.repositories.UserRepo;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.UserPrincipal;
import org.STPP.AgriAutomation.data.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepo repo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Users register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (repo.findByUsername(registerRequest.getUsername()) != null) {
            logger.warn("Attempt to register with existing username: {}", registerRequest.getUsername());
            throw new RuntimeException("Username is already taken");
        }

        // Fetch the USER role from the database
        Optional<Role> userRoleOptional = roleRepository.findByName("USER");
        if (!userRoleOptional.isPresent()) {
            logger.error("USER role not found in the database");
            throw new RuntimeException("USER role not found");
        }
        Role userRole = userRoleOptional.get();

        // Create the Users entity
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Assign the USER role
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Save the user to the database
        repo.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
        return user;
    }


    public String verify(LoginRequest loginRequest) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return jwtService.generateToken(userPrincipal);
            }
        } catch (AuthenticationException e) {
            // Log authentication failure
        }
        return "fail";
    }
}
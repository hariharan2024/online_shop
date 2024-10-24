package com.example.PRODUCT_SERVICE.SERVICE;



import com.example.PRODUCT_SERVICE.MODEL.Users;
import com.example.PRODUCT_SERVICE.REPOSITY.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    public Users registerUser(Users user) {
        logger.info("Registering user: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Registration failed: Username '{}' already exists.", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the new user to the database
        Users savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    public String verify(Users user) {
        logger.info("Verifying user: {}", user.getUsername());

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                logger.info("User authenticated successfully");

                Users authenticatedUser = userRepository.findByUsername(user.getUsername());
                logger.info("Fetching roles for user: {}", authenticatedUser.getRoles());

                return jwtService.generateToken(user.getUsername(), authenticatedUser.getRoles());
            }
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", user.getUsername(), e);
            return "fail";
        }
        return "fail";
    }

}
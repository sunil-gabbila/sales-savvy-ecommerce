package com.kodnest.salessavvy.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.JWTToken;
import com.kodnest.salessavvy.entity.User;
import com.kodnest.salessavvy.repository.JWTTokenRepository;
import com.kodnest.salessavvy.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final Key SIGNING_KEY;

    private final UserRepository userRepository;
    private final JWTTokenRepository jwtTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Injecting the application-specific JWT secret from properties.
    public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository,
                       @Value("${salessavvy.jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();

        // Ensure the key length is at least 64 bytes
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalArgumentException("SALESSAVVY_JWT_SECRET must be at least 64 bytes long for HS512.");
        }
        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public User authenticate(String username, String password) {
        log.info("Authentication service started");
        User user;
        try {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        } catch (DataAccessException e) {
            log.error("Could not retrieve user credentials from the database", e);
            throw e;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        log.info("Authentication credentials validated");
        return user;
    }

    public String generateToken(User user) {
        log.info("Generating or reusing an authentication token");
        try {
            String token;
            LocalDateTime now = LocalDateTime.now();
            JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());

            if (existingToken != null && now.isBefore(existingToken.getExpiresAt())) {
                token = existingToken.getToken();
            } else {
                token = generateNewToken(user);
                if (existingToken != null) {
                    jwtTokenRepository.delete(existingToken);
                }
                saveToken(user, token);
            }
            log.info("Authentication token is ready");
            return token;
        } catch (DataAccessException e) {
            log.error("Could not persist or retrieve the authentication token", e);
            throw e;
        }
    }

    private String generateNewToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public void saveToken(User user, String token) {
        JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));
        jwtTokenRepository.save(jwtToken);
        log.info("Authentication token saved");
    }
}
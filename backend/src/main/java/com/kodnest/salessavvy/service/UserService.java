package com.kodnest.salessavvy.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kodnest.salessavvy.dto.RegistrationRequest;
import com.kodnest.salessavvy.dto.UserDetailsResponse;
import com.kodnest.salessavvy.entity.Role;
import com.kodnest.salessavvy.entity.User;
import com.kodnest.salessavvy.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

    @Transactional
	public String registerUser(RegistrationRequest request) {
        log.info("Registration service started");
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration details are required");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username, email, and password are required");
        }
        if (!request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email address is required");
        }
        log.info("Registration request validation passed");

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration rejected because the username is already taken");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration rejected because the email is already registered");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(parseRole(request.getRole()));

        log.info("Saving registered user");
        User savedUser = userRepository.save(user);
        log.info("Registration database save completed");
        return savedUser.getUsername();
	}

    @Transactional(readOnly = true)
    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDetailsResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.CUSTOMER;
        }

        try {
            return Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be ADMIN or CUSTOMER");
        }
    }

}

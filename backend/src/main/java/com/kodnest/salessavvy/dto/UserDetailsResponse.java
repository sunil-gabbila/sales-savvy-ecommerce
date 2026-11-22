package com.kodnest.salessavvy.dto;

import java.time.LocalDateTime;

import com.kodnest.salessavvy.entity.Role;

public record UserDetailsResponse(
        Integer userId,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
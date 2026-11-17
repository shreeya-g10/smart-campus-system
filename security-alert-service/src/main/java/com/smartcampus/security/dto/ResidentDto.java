package com.smartcampus.security.dto;

/** Copy of RESIDENT-SERVICE's Resident (see API_CONTRACT.md). */
public record ResidentDto(
        Long id,
        String name,
        String roomNumber,
        String phone,
        String email,
        boolean active) {
}

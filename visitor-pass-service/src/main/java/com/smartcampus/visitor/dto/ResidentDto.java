package com.smartcampus.visitor.dto;

public record ResidentDto(
        Long id,
        String name,
        String roomNumber,
        String phone,
        String email,
        boolean active) {
}

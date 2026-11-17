package com.smartcampus.visitor.dto;

public record CreatePassRequest(
        String visitorName,
        String visitorPhone,
        String purpose,
        Long hostId) {
}

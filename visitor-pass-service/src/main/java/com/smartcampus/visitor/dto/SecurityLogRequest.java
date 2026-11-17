package com.smartcampus.visitor.dto;

public record SecurityLogRequest(
        Long passId,
        String visitorName,
        Long hostId,
        String eventType,
        String message) {
}

package com.smartcampus.visitor.dto;

public record SecurityAlertRequest(
        String alertType,
        String message,
        String severity) {
}

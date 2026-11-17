package com.smartcampus.security.dto;

import java.time.LocalDateTime;

/** Copy of VISITOR-PASS-SERVICE's VisitorPass (see API_CONTRACT.md). */
public record VisitorPassDto(
        Long id,
        String visitorName,
        String visitorPhone,
        String purpose,
        Long hostId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime checkInTime,
        LocalDateTime checkOutTime) {
}

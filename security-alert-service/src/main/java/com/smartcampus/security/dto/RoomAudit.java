package com.smartcampus.security.dto;

import java.util.List;

/** Response of GET /security/audit/room/{roomNumber}. */
public record RoomAudit(
        ResidentDto resident,
        List<VisitorPassDto> activeVisitors) {
}

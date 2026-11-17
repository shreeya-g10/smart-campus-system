package com.smartcampus.security.controller;

import com.smartcampus.security.client.ResidentClient;
import com.smartcampus.security.client.VisitorPassClient;
import com.smartcampus.security.dto.ResidentDto;
import com.smartcampus.security.dto.RoomAudit;
import com.smartcampus.security.dto.VisitorPassDto;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/security/audit")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    private final ResidentClient residentClient;
    private final VisitorPassClient visitorPassClient;

    public AuditController(ResidentClient residentClient, VisitorPassClient visitorPassClient) {
        this.residentClient = residentClient;
        this.visitorPassClient = visitorPassClient;
    }

    /**
     * Room security audit: who lives in the room (RESIDENT-SERVICE)
     * and which visitors for that resident are currently active (VISITOR-PASS-SERVICE).
     */
    @GetMapping("/room/{roomNumber}")
    public RoomAudit auditRoom(@PathVariable String roomNumber) {
        ResidentDto resident;
        try {
            resident = residentClient.getByRoom(roomNumber);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No resident in room " + roomNumber);
        } catch (RuntimeException e) {
            log.warn("RESIDENT-SERVICE unavailable for room {}: {}", roomNumber, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Resident service unavailable");
        }

        List<VisitorPassDto> activeVisitors;
        try {
            activeVisitors = visitorPassClient.getActiveByHost(resident.id());
        } catch (RuntimeException e) {
            // Visitor service down or endpoint not deployed yet: still return the resident.
            log.warn("VISITOR-PASS-SERVICE unavailable for host {}: {}", resident.id(), e.getMessage());
            activeVisitors = List.of();
        }

        return new RoomAudit(resident, activeVisitors);
    }
}

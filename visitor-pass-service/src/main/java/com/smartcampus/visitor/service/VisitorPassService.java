package com.smartcampus.visitor.service;

import com.smartcampus.visitor.client.ResidentClient;
import com.smartcampus.visitor.client.SecurityAlertClient;
import com.smartcampus.visitor.dto.CreatePassRequest;
import com.smartcampus.visitor.dto.ResidentDto;
import com.smartcampus.visitor.dto.SecurityAlertRequest;
import com.smartcampus.visitor.dto.SecurityLogRequest;
import com.smartcampus.visitor.entity.VisitorPass;
import com.smartcampus.visitor.repository.VisitorPassRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorPassService {

    private static final Logger log = LoggerFactory.getLogger(VisitorPassService.class);

    private final VisitorPassRepository repository;
    private final ResidentClient residentClient;
    private final SecurityAlertClient securityAlertClient;

    public VisitorPassService(VisitorPassRepository repository,
                              ResidentClient residentClient,
                              SecurityAlertClient securityAlertClient) {
        this.repository = repository;
        this.residentClient = residentClient;
        this.securityAlertClient = securityAlertClient;
    }

    public VisitorPass createPass(CreatePassRequest request) {
        if (request == null || request.visitorName() == null || request.visitorName().isBlank()
                || request.hostId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "visitorName and hostId are required");
        }

        ResidentDto resident = null;
        try {
            resident = residentClient.getById(request.hostId());
        } catch (FeignException.NotFound e) {
            log.warn("Host resident with id {} not found", request.hostId());
            resident = null;
        } catch (Exception e) {
            log.warn("Failed to fetch resident with id {}: {}", request.hostId(), e.getMessage());
            resident = null;
        }

        boolean isHostAuthorized = (resident != null && resident.active());
        String status = isHostAuthorized ? "APPROVED" : "REJECTED";

        VisitorPass pass = new VisitorPass(
                request.visitorName(),
                request.visitorPhone(),
                request.purpose(),
                request.hostId(),
                status
        );

        VisitorPass savedPass = repository.save(pass);

        if (isHostAuthorized) {
            sendLogQuietly(new SecurityLogRequest(
                    savedPass.getId(),
                    savedPass.getVisitorName(),
                    savedPass.getHostId(),
                    "PASS_CREATED",
                    "Pass created and approved for visitor " + savedPass.getVisitorName() + " (host: " + resident.name() + ")"
            ));
        } else {
            String reason = (resident == null)
                    ? "Resident not found (id: " + request.hostId() + ")"
                    : "Resident is inactive (id: " + request.hostId() + ", name: " + resident.name() + ")";

            sendAlertQuietly(new SecurityAlertRequest(
                    "UNAUTHORIZED_HOST_ATTEMPT",
                    "Unauthorized visitor pass attempt for hostId " + request.hostId() + ": " + reason,
                    "HIGH"
            ));
        }

        return savedPass;
    }

    public VisitorPass checkIn(Long id) {
        VisitorPass pass = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Visitor pass " + id + " not found"));

        if (!"APPROVED".equals(pass.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Check-in only allowed for APPROVED passes, current status: " + pass.getStatus());
        }

        pass.setStatus("CHECKED_IN");
        pass.setCheckInTime(LocalDateTime.now());
        VisitorPass updated = repository.save(pass);

        sendLogQuietly(new SecurityLogRequest(
                updated.getId(),
                updated.getVisitorName(),
                updated.getHostId(),
                "CHECK_IN",
                "Visitor " + updated.getVisitorName() + " checked in"
        ));

        return updated;
    }

    public VisitorPass checkOut(Long id) {
        VisitorPass pass = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Visitor pass " + id + " not found"));

        if (!"CHECKED_IN".equals(pass.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Check-out only allowed for CHECKED_IN passes, current status: " + pass.getStatus());
        }

        pass.setStatus("CHECKED_OUT");
        pass.setCheckOutTime(LocalDateTime.now());
        VisitorPass updated = repository.save(pass);

        sendLogQuietly(new SecurityLogRequest(
                updated.getId(),
                updated.getVisitorName(),
                updated.getHostId(),
                "CHECK_OUT",
                "Visitor " + updated.getVisitorName() + " checked out"
        ));

        return updated;
    }

    public List<VisitorPass> getAllPasses() {
        return repository.findAll();
    }

    public VisitorPass getPassById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Visitor pass " + id + " not found"));
    }

    public List<VisitorPass> getActivePassesByHost(Long hostId) {
        return repository.findByHostIdAndStatusIn(hostId, List.of("APPROVED", "CHECKED_IN"));
    }

    private void sendLogQuietly(SecurityLogRequest logRequest) {
        try {
            securityAlertClient.createLog(logRequest);
        } catch (Exception e) {
            log.warn("Failed to send security log: {}", e.getMessage());
        }
    }

    private void sendAlertQuietly(SecurityAlertRequest alertRequest) {
        try {
            securityAlertClient.createAlert(alertRequest);
        } catch (Exception e) {
            log.warn("Failed to send security alert: {}", e.getMessage());
        }
    }
}

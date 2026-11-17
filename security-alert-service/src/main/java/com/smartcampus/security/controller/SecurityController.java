package com.smartcampus.security.controller;

import com.smartcampus.security.entity.SecurityAlert;
import com.smartcampus.security.entity.SecurityLog;
import com.smartcampus.security.repository.SecurityAlertRepository;
import com.smartcampus.security.repository.SecurityLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityLogRepository logRepository;
    private final SecurityAlertRepository alertRepository;

    public SecurityController(SecurityLogRepository logRepository, SecurityAlertRepository alertRepository) {
        this.logRepository = logRepository;
        this.alertRepository = alertRepository;
    }

    // ---------- Gate logs (PASS_CREATED, CHECK_IN, CHECK_OUT) ----------

    @PostMapping("/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityLog createLog(@RequestBody SecurityLog log) {
        if (log.getEventType() == null || log.getEventType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventType is required");
        }
        log.setId(null);
        return logRepository.save(log);
    }

    @GetMapping("/logs")
    public List<SecurityLog> getLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }

    // ---------- Security alerts (UNAUTHORIZED_HOST_ATTEMPT, ...) ----------

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityAlert createAlert(@RequestBody SecurityAlert alert) {
        if (alert.getAlertType() == null || alert.getAlertType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "alertType is required");
        }
        alert.setId(null);
        return alertRepository.save(alert);
    }

    @GetMapping("/alerts")
    public List<SecurityAlert> getAlerts() {
        return alertRepository.findAllByOrderByTimestampDesc();
    }
}

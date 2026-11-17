package com.smartcampus.visitor.controller;

import com.smartcampus.visitor.dto.CreatePassRequest;
import com.smartcampus.visitor.entity.VisitorPass;
import com.smartcampus.visitor.service.VisitorPassService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visitors/passes")
public class VisitorPassController {

    private final VisitorPassService service;

    public VisitorPassController(VisitorPassService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitorPass createPass(@RequestBody CreatePassRequest request) {
        return service.createPass(request);
    }

    @GetMapping
    public List<VisitorPass> getAllPasses() {
        return service.getAllPasses();
    }

    @GetMapping("/{id}")
    public VisitorPass getPassById(@PathVariable Long id) {
        return service.getPassById(id);
    }

    @PutMapping("/{id}/checkin")
    public VisitorPass checkIn(@PathVariable Long id) {
        return service.checkIn(id);
    }

    @PutMapping("/{id}/checkout")
    public VisitorPass checkOut(@PathVariable Long id) {
        return service.checkOut(id);
    }

    @GetMapping("/active/host/{hostId}")
    public List<VisitorPass> getActivePassesByHost(@PathVariable Long hostId) {
        return service.getActivePassesByHost(hostId);
    }
}

package com.smartcampus.resident.controller;

import com.smartcampus.resident.entity.Resident;
import com.smartcampus.resident.repository.ResidentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/residents")
public class ResidentController {

    private final ResidentRepository repository;

    public ResidentController(ResidentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Resident> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Resident getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resident " + id + " not found"));
    }

    @GetMapping("/room/{roomNumber}")
    public Resident getByRoom(@PathVariable String roomNumber) {
        return repository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No resident in room " + roomNumber));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resident create(@RequestBody Resident resident) {
        if (resident.getName() == null || resident.getName().isBlank()
                || resident.getRoomNumber() == null || resident.getRoomNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name and roomNumber are required");
        }
        if (repository.existsByRoomNumber(resident.getRoomNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Room " + resident.getRoomNumber() + " is already occupied");
        }
        resident.setId(null);
        return repository.save(resident);
    }
}

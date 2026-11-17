package com.smartcampus.resident.repository;

import com.smartcampus.resident.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);
}

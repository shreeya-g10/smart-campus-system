package com.smartcampus.visitor.repository;

import com.smartcampus.visitor.entity.VisitorPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorPassRepository extends JpaRepository<VisitorPass, Long> {

    List<VisitorPass> findByHostIdAndStatusIn(Long hostId, List<String> statuses);
}

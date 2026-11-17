package com.smartcampus.security.client;

import com.smartcampus.security.dto.VisitorPassDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "VISITOR-PASS-SERVICE")
public interface VisitorPassClient {

    @GetMapping("/visitors/passes/active/host/{hostId}")
    List<VisitorPassDto> getActiveByHost(@PathVariable("hostId") Long hostId);
}

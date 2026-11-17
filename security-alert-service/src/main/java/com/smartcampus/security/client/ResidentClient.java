package com.smartcampus.security.client;

import com.smartcampus.security.dto.ResidentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RESIDENT-SERVICE")
public interface ResidentClient {

    @GetMapping("/residents/room/{roomNumber}")
    ResidentDto getByRoom(@PathVariable("roomNumber") String roomNumber);
}

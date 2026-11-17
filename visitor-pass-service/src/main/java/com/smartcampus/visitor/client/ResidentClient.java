package com.smartcampus.visitor.client;

import com.smartcampus.visitor.dto.ResidentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RESIDENT-SERVICE")
public interface ResidentClient {

    @GetMapping("/residents/{id}")
    ResidentDto getById(@PathVariable("id") Long id);
}

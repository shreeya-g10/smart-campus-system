package com.smartcampus.visitor.client;

import com.smartcampus.visitor.dto.SecurityAlertRequest;
import com.smartcampus.visitor.dto.SecurityLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "SECURITY-ALERT-SERVICE")
public interface SecurityAlertClient {

    @PostMapping("/security/logs")
    void createLog(@RequestBody SecurityLogRequest logRequest);

    @PostMapping("/security/alerts")
    void createAlert(@RequestBody SecurityAlertRequest alertRequest);
}

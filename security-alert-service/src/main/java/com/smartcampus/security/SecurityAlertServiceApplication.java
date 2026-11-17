package com.smartcampus.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SecurityAlertServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityAlertServiceApplication.class, args);
	}

}

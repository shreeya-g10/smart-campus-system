package com.smartcampus.visitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VisitorPassServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitorPassServiceApplication.class, args);
	}

}

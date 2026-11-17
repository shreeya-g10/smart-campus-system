package com.smartcampus.resident.config;

import com.smartcampus.resident.entity.Resident;
import com.smartcampus.resident.repository.ResidentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedResidents(ResidentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new Resident("Aarav Sharma", "A-101", "9876500001", "aarav@campus.edu", true),
                    new Resident("Diya Patel", "A-102", "9876500002", "diya@campus.edu", true),
                    // inactive on purpose: used to demo a REJECTED visitor pass
                    new Resident("Rohan Iyer", "B-205", "9876500003", "rohan@campus.edu", false)
            ));
        };
    }
}

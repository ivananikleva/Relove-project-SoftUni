package com.relove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReLoveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReLoveApplication.class, args);
    }
}

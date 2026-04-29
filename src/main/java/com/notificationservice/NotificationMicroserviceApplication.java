package com.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationMicroserviceApplication.class, args);
    }
}

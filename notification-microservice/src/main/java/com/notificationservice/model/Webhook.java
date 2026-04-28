package com.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "webhooks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String appName;

    @Column(nullable = false)
    private String eventType; // e.g. ORDER_PLACED, USER_SIGNUP

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel; // EMAIL, SMS, PUSH

    @Column(nullable = false)
    private String target; // email / phone number / device token

    @Column(nullable = false, unique = true)
    private String secretKey; // used by apps to authenticate

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.secretKey == null) {
            this.secretKey = UUID.randomUUID().toString().replace("-", "");
        }
    }
}

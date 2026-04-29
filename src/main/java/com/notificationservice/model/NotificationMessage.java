package com.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private UUID eventId;
    private UUID webhookId;
    private String eventType;
    private String target;
    private NotificationChannel channel;
    private Map<String, Object> payload;
    private String appName;
}

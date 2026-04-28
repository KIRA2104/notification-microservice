package com.notificationservice.channel;

import com.notificationservice.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsChannel {

    public void send(NotificationMessage message) {
        // Simulate occasional failure (10% chance)
        if (Math.random() < 0.1) {
            throw new RuntimeException("SMS gateway unreachable - delivery failed");
        }

        log.info("✅ SMS SENT | To: {} | Event: {} | App: {} | Payload: {}",
                message.getTarget(),
                message.getEventType(),
                message.getAppName(),
                message.getPayload());
    }
}

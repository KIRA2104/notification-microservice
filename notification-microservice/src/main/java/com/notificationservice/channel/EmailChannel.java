package com.notificationservice.channel;

import com.notificationservice.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailChannel {

    public void send(NotificationMessage message) {
        // Simulate occasional failure (20% chance) for demo purposes
        if (Math.random() < 0.2) {
            throw new RuntimeException("SMTP connection timeout - email delivery failed");
        }

        log.info("✅ EMAIL SENT | To: {} | Event: {} | App: {} | Payload: {}",
                message.getTarget(),
                message.getEventType(),
                message.getAppName(),
                message.getPayload());
    }
}

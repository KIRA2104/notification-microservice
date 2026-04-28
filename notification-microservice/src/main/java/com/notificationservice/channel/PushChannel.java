package com.notificationservice.channel;

import com.notificationservice.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushChannel {

    public void send(NotificationMessage message) {
        // Simulate occasional failure (15% chance)
        if (Math.random() < 0.15) {
            throw new RuntimeException("Push notification service unavailable");
        }

        log.info("✅ PUSH SENT | Device Token: {} | Event: {} | App: {} | Payload: {}",
                message.getTarget(),
                message.getEventType(),
                message.getAppName(),
                message.getPayload());
    }
}

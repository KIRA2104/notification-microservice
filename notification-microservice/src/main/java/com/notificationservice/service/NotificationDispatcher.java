package com.notificationservice.service;

import com.notificationservice.channel.EmailChannel;
import com.notificationservice.channel.PushChannel;
import com.notificationservice.channel.SmsChannel;
import com.notificationservice.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcher {

    private final EmailChannel emailChannel;
    private final SmsChannel smsChannel;
    private final PushChannel pushChannel;

    /**
     * Routes message to the correct channel based on its type.
     */
    public void dispatch(NotificationMessage message) {
        log.info("Dispatching notification | Channel: {} | EventType: {} | Target: {}",
                message.getChannel(), message.getEventType(), message.getTarget());

        switch (message.getChannel()) {
            case EMAIL -> emailChannel.send(message);
            case SMS   -> smsChannel.send(message);
            case PUSH  -> pushChannel.send(message);
            default    -> throw new IllegalArgumentException("Unknown channel: " + message.getChannel());
        }
    }
}

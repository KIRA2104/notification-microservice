package com.notificationservice.consumer;

import com.notificationservice.config.RabbitMQConfig;
import com.notificationservice.model.DeliveryLog;
import com.notificationservice.model.DeliveryStatus;
import com.notificationservice.model.NotificationMessage;
import com.notificationservice.repository.DeliveryLogRepository;
import com.notificationservice.repository.WebhookRepository;
import com.notificationservice.repository.EventRepository;
import com.notificationservice.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationDispatcher dispatcher;
    private final DeliveryLogRepository deliveryLogRepository;
    private final EventRepository eventRepository;
    private final WebhookRepository webhookRepository;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmail(NotificationMessage message) {
        processMessage(message);
    }

    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void handleSms(NotificationMessage message) {
        processMessage(message);
    }

    @RabbitListener(queues = RabbitMQConfig.PUSH_QUEUE)
    public void handlePush(NotificationMessage message) {
        processMessage(message);
    }

    private void processMessage(NotificationMessage message) {
        // Find the corresponding delivery log
        DeliveryLog deliveryLog = findDeliveryLog(message);
        if (deliveryLog == null) {
            log.error("No delivery log found for eventId: {} webhookId: {}",
                    message.getEventId(), message.getWebhookId());
            return;
        }

        deliveryLog.setAttemptCount(deliveryLog.getAttemptCount() + 1);
        deliveryLog.setLastAttemptedAt(LocalDateTime.now());

        try {
            dispatcher.dispatch(message);
            deliveryLog.setStatus(DeliveryStatus.SUCCESS);
            log.info("✅ Delivery successful | Channel: {} | Target: {}",
                    message.getChannel(), message.getTarget());
        } catch (Exception e) {
            deliveryLog.setStatus(DeliveryStatus.FAILED);
            deliveryLog.setFailureReason(e.getMessage());
            log.error("❌ Delivery failed | Channel: {} | Target: {} | Reason: {}",
                    message.getChannel(), message.getTarget(), e.getMessage());
        }

        deliveryLogRepository.save(deliveryLog);
    }

    private DeliveryLog findDeliveryLog(NotificationMessage message) {
        return eventRepository.findById(message.getEventId())
                .flatMap(event -> webhookRepository.findById(message.getWebhookId())
                        .map(webhook -> {
                            List<DeliveryLog> logs = deliveryLogRepository.findByEvent(event);
                            return logs.stream()
                                    .filter(l -> l.getWebhook().getId().equals(webhook.getId()))
                                    .findFirst()
                                    .orElse(null);
                        }))
                .orElse(null);
    }
}

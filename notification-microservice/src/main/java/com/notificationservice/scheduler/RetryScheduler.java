package com.notificationservice.scheduler;

import com.notificationservice.config.RabbitMQConfig;
import com.notificationservice.model.*;
import com.notificationservice.repository.DeliveryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryScheduler {

    private final DeliveryLogRepository deliveryLogRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${retry.max-attempts:3}")
    private int maxAttempts;

    /**
     * Runs every 60 seconds - picks up failed deliveries and re-queues them.
     */
    @Scheduled(fixedDelayString = "${retry.interval-ms:60000}")
    public void retryFailedDeliveries() {
        List<DeliveryLog> failedLogs = deliveryLogRepository
                .findByStatusAndAttemptCountLessThan(DeliveryStatus.FAILED, maxAttempts);

        if (failedLogs.isEmpty()) {
            return;
        }

        log.info("Retry scheduler running | Found {} failed deliveries to retry", failedLogs.size());

        for (DeliveryLog log : failedLogs) {
            Event event = log.getEvent();
            Webhook webhook = log.getWebhook();

            // Build retry message
            NotificationMessage retryMessage = NotificationMessage.builder()
                    .eventId(event.getId())
                    .webhookId(webhook.getId())
                    .eventType(event.getEventType())
                    .target(webhook.getTarget())
                    .channel(webhook.getChannel())
                    .payload(event.getPayload())
                    .appName(event.getSourceApp())
                    .build();

            // Re-queue to RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    webhook.getChannel().name(),
                    retryMessage
            );

            // If this was the last allowed retry, mark as permanently failed
            if (log.getAttemptCount() + 1 >= maxAttempts) {
                log.setStatus(DeliveryStatus.PERMANENTLY_FAILED);
                deliveryLogRepository.save(log);
                this.log.warn("⛔ Permanently failed | DeliveryLog: {} | Target: {}",
                        log.getId(), webhook.getTarget());
            } else {
                this.log.info("🔄 Retrying delivery | DeliveryLog: {} | Attempt: {}/{}",
                        log.getId(), log.getAttemptCount() + 1, maxAttempts);
            }
        }
    }
}

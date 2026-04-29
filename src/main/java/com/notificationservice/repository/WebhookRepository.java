package com.notificationservice.repository;

import com.notificationservice.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, UUID> {

    List<Webhook> findByEventTypeAndActiveTrue(String eventType);

    Optional<Webhook> findBySecretKey(String secretKey);

    List<Webhook> findByAppName(String appName);
}

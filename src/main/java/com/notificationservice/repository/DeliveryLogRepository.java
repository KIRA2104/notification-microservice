package com.notificationservice.repository;

import com.notificationservice.model.DeliveryLog;
import com.notificationservice.model.DeliveryStatus;
import com.notificationservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, UUID> {

    List<DeliveryLog> findByEvent(Event event);

    List<DeliveryLog> findByStatus(DeliveryStatus status);

    List<DeliveryLog> findByStatusAndAttemptCountLessThan(DeliveryStatus status, int maxAttempts);

    long countByStatus(DeliveryStatus status);
}

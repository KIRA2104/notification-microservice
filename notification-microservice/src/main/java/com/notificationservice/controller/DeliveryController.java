package com.notificationservice.controller;

import com.notificationservice.model.DeliveryLog;
import com.notificationservice.model.DeliveryStatus;
import com.notificationservice.model.Event;
import com.notificationservice.repository.DeliveryLogRepository;
import com.notificationservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryLogRepository deliveryLogRepository;
    private final EventService eventService;

    // GET /api/deliveries/{eventId} - Track all deliveries for an event
    @GetMapping("/api/deliveries/{eventId}")
    public ResponseEntity<List<DeliveryLog>> getDeliveriesForEvent(@PathVariable UUID eventId) {
        Event event = eventService.getById(eventId);
        return ResponseEntity.ok(deliveryLogRepository.findByEvent(event));
    }

    // GET /api/deliveries/failed - All failed deliveries
    @GetMapping("/api/deliveries/failed")
    public ResponseEntity<List<DeliveryLog>> getFailedDeliveries() {
        return ResponseEntity.ok(deliveryLogRepository.findByStatus(DeliveryStatus.FAILED));
    }

    // GET /api/deliveries/permanently-failed - Deliveries that exhausted retries
    @GetMapping("/api/deliveries/permanently-failed")
    public ResponseEntity<List<DeliveryLog>> getPermanentlyFailed() {
        return ResponseEntity.ok(deliveryLogRepository.findByStatus(DeliveryStatus.PERMANENTLY_FAILED));
    }

    /**
     * GET /api/dashboard/stats
     * Returns overall system statistics.
     */
    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long total = deliveryLogRepository.count();
        long success = deliveryLogRepository.countByStatus(DeliveryStatus.SUCCESS);
        long failed = deliveryLogRepository.countByStatus(DeliveryStatus.FAILED);
        long pending = deliveryLogRepository.countByStatus(DeliveryStatus.PENDING);
        long permanentlyFailed = deliveryLogRepository.countByStatus(DeliveryStatus.PERMANENTLY_FAILED);

        double successRate = total > 0 ? Math.round((double) success / total * 1000.0) / 10.0 : 0.0;

        return ResponseEntity.ok(Map.of(
                "totalDeliveries", total,
                "successful", success,
                "failed", failed,
                "pending", pending,
                "permanentlyFailed", permanentlyFailed,
                "successRatePercent", successRate
        ));
    }
}

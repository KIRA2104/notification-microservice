package com.notificationservice.controller;

import com.notificationservice.model.Event;
import com.notificationservice.model.Webhook;
import com.notificationservice.repository.WebhookRepository;
import com.notificationservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final WebhookRepository webhookRepository;

    /**
     * POST /api/events
     * Client apps call this to fire an event.
     * Requires X-Secret-Key header for authentication.
     */
    @PostMapping
    public ResponseEntity<?> receiveEvent(
            @RequestBody Event event,
            @RequestHeader("X-Secret-Key") String secretKey) {

        // Authenticate the caller using secret key
        Optional<Webhook> webhook = webhookRepository.findBySecretKey(secretKey);
        if (webhook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid secret key"));
        }

        // Set the source app from the authenticated webhook
        event.setSourceApp(webhook.get().getAppName());

        Event saved = eventService.processEvent(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "eventId", saved.getId(),
                "status", "QUEUED",
                "message", "Event received and queued for delivery"
        ));
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getById(id));
    }
}

package com.notificationservice.controller;

import com.notificationservice.model.Webhook;
import com.notificationservice.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    // POST /api/webhooks - Register a new webhook
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody Webhook webhook) {
        Webhook saved = webhookService.register(webhook);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "webhookId", saved.getId(),
                "secretKey", saved.getSecretKey(),
                "message", "Webhook registered successfully"
        ));
    }

    // GET /api/webhooks/{id} - Get webhook by ID
    @GetMapping("/{id}")
    public ResponseEntity<Webhook> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(webhookService.getById(id));
    }

    // GET /api/webhooks?appName=ShopEasy - Get all webhooks for an app
    @GetMapping
    public ResponseEntity<List<Webhook>> getByApp(@RequestParam String appName) {
        return ResponseEntity.ok(webhookService.getByApp(appName));
    }

    // PUT /api/webhooks/{id} - Update webhook
    @PutMapping("/{id}")
    public ResponseEntity<Webhook> update(@PathVariable UUID id, @RequestBody Webhook webhook) {
        return ResponseEntity.ok(webhookService.update(id, webhook));
    }

    // DELETE /api/webhooks/{id} - Deactivate webhook
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable UUID id) {
        webhookService.deactivate(id);
        return ResponseEntity.ok(Map.of("message", "Webhook deactivated successfully"));
    }
}

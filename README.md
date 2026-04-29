# 🔔 Event-Driven Notification Microservice

A standalone backend service that allows any app to register webhooks and receive
notifications across multiple channels (Email, SMS, Push) when events occur.

---

## Architecture Overview

```
Client App → POST /api/events → EventService (fan-out)
                                      ↓
                              RabbitMQ (3 queues)
                          EMAIL | SMS | PUSH
                                      ↓
                         NotificationConsumer (listener)
                                      ↓
                         Channel simulation + DeliveryLog
                                      ↓
                         RetryScheduler (every 60s)
```

---

## Tech Stack

| Technology     | Purpose                        |
|----------------|--------------------------------|
| Spring Boot    | Core framework                 |
| PostgreSQL     | Store webhooks, events, logs   |
| RabbitMQ       | Async fan-out message queue    |
| Redis          | Rate limiting per app          |
| Spring Scheduler | Retry failed deliveries      |
| Lombok         | Reduce boilerplate             |

---

## Prerequisites

Make sure these are installed and running:

- Java 17+
- Maven 3.8+
- PostgreSQL (running on port 5432)
- RabbitMQ (running on port 5672)
- Redis (running on port 6379)

### Quick setup with Docker (recommended)

```bash
# Start PostgreSQL
docker run --name postgres -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=notificationdb -p 5432:5432 -d postgres

# Start RabbitMQ with management UI
docker run --name rabbitmq -p 5672:5672 -p 15672:15672 -d rabbitmq:management

# Start Redis
docker run --name redis -p 6379:6379 -d redis
```

---

## Running the Project

```bash
# Clone and navigate
cd notification-microservice

# Update DB credentials in src/main/resources/application.properties

# Build and run
mvn spring-boot:run
```

Server starts on: `http://localhost:8080`

---

## API Reference & Postman Demo Flow

### Step 1 — Register a Webhook (EMAIL)

```
POST http://localhost:8080/api/webhooks
Content-Type: application/json

{
  "appName": "ShopEasy",
  "eventType": "ORDER_PLACED",
  "channel": "EMAIL",
  "target": "customer@example.com"
}
```

**Response:**
```json
{
  "webhookId": "uuid-here",
  "secretKey": "abc123xyz",
  "message": "Webhook registered successfully"
}
```

> Save the `secretKey` — you need it to fire events.

---

### Step 2 — Register a Second Webhook (SMS) for same event

```
POST http://localhost:8080/api/webhooks

{
  "appName": "ShopEasy",
  "eventType": "ORDER_PLACED",
  "channel": "SMS",
  "target": "+919876543210"
}
```

---

### Step 3 — Fire an Event (Fan-out happens here!)

```
POST http://localhost:8080/api/events
Content-Type: application/json
X-Secret-Key: abc123xyz

{
  "eventType": "ORDER_PLACED",
  "payload": {
    "orderId": "ORD-999",
    "amount": 1499,
    "item": "Wireless Headphones"
  }
}
```

**Response:**
```json
{
  "eventId": "event-uuid",
  "status": "QUEUED",
  "message": "Event received and queued for delivery"
}
```

> Watch the **server logs** — you'll see both EMAIL and SMS notifications fire simultaneously!

---

### Step 4 — Track Delivery Status

```
GET http://localhost:8080/api/deliveries/{eventId}
```

Shows per-channel delivery status, attempt count, and failure reasons.

---

### Step 5 — View Dashboard Stats

```
GET http://localhost:8080/api/dashboard/stats
```

**Response:**
```json
{
  "totalDeliveries": 10,
  "successful": 8,
  "failed": 1,
  "pending": 0,
  "permanentlyFailed": 1,
  "successRatePercent": 80.0
}
```

---

### Step 6 — View Failed Deliveries

```
GET http://localhost:8080/api/deliveries/failed
```

---

### Step 7 — Update or Deactivate a Webhook

```
PUT http://localhost:8080/api/webhooks/{webhookId}
{
  "target": "newemail@example.com",
  "channel": "EMAIL",
  "eventType": "ORDER_PLACED"
}

DELETE http://localhost:8080/api/webhooks/{webhookId}
```

---

## Key Concepts Demonstrated

| Concept                    | Where                          |
|----------------------------|--------------------------------|
| Event-Driven Architecture  | EventService → RabbitMQ fan-out |
| Microservice Design        | Standalone pluggable service   |
| Async Processing           | RabbitMQ consumers             |
| Fault Tolerance            | RetryScheduler (3 attempts)    |
| Rate Limiting              | Redis per-app throttling       |
| Observability              | DeliveryLogs + Dashboard API   |
| Authentication             | Secret key per webhook         |

---

## Simulated Failure Rates (for demo)

| Channel | Failure Rate |
|---------|-------------|
| EMAIL   | 20%         |
| SMS     | 10%         |
| PUSH    | 15%         |

This makes it easy to demo retry logic during your presentation.

---

## RabbitMQ Management UI

Visit `http://localhost:15672` (user: guest, pass: guest) to visually see your queues.

---

## Project Structure

```
src/main/java/com/notificationservice/
├── controller/        ← REST API endpoints
├── service/           ← Business logic
├── channel/           ← Email, SMS, Push simulators
├── consumer/          ← RabbitMQ queue listeners
├── model/             ← JPA entities & enums
├── repository/        ← Database access
├── scheduler/         ← Retry logic
└── config/            ← RabbitMQ, Redis, Exception handler
```

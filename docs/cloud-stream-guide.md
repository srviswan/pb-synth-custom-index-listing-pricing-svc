# Spring Cloud Stream Technical Guide

This document explains how the **Spring Cloud Stream** integration is architected in the Custom Index Basket Management Platform.

## 1. Functional Programming Model

The project uses the **Java Functional Model** introduced in Spring Cloud Stream 3.x. Instead of using legacy annotations like `@StreamListener`, we define standard Java functional beans.

### Entry Point: `StreamConfig.java`
In each service, the `StreamConfig` class defines `Consumer<T>` or `Function<T, R>` beans.

```java
@Bean
public Consumer<EventEnvelope<BasketCreatedEvent>> basketCreated(ListingService service) {
    return envelope -> {
        // Business logic delegation
        service.handleBasketCreated(envelope.getPayload());
    };
}
```

## 2. Event Envelope Pattern

Every event in the system is wrapped in a generic `EventEnvelope<T>`. This ensures a standardized metadata schema across all services.

- **Payload**: The actual business event (e.g., `BasketCreatedEvent`).
- **TraceId**: Correlation ID for end-to-end distributed tracing.
- **IdempotencyKey**: Prevents duplicate processing of the same message.
- **OccurredAt**: Precise timestamp of the event origin.

## 3. Configuration Mapping (`application.yml`)

The connection between the Java beans and the messaging broker (Kafka/Solace) is defined via **Bindings**.

```yaml
spring:
  cloud:
    function:
      definition: basketCreated # Maps to the @Bean name
    stream:
      bindings:
        basketCreated-in-0: # Convention: <beanName>-in-<index>
          destination: basket.created
          group: listing-group
```

## 4. Broker Agnostic Architecture (Kafka vs Solace)

The system is designed to be **Broker Agnostic**. You can switch between Kafka and Solace by changing dependencies and YAML configuration without modifying any Java code.

### Switching to Solace
1. **Dependency**: Swap `spring-cloud-starter-stream-kafka` for `spring-cloud-starter-stream-solace`.
2. **Binder Config**: Change `default-binder` to `solace` and update the connection details (Host, VPN).
3. **Behavior**: The code continues to function because the `Consumer` interface is independent of the underlying transport.

## 5. Resiliency & Error Handling

We have implemented three layers of resiliency:

1.  **Retries**: Configured via `max-attempts`. If processing fails, the binder retries with **Exponential Backoff**.
2.  **Circuit Breaker**: Integrated via Resilience4j for downstream REST calls (e.g., RefData validation).
3.  **Dead Letter Queue (DLQ)**: If all retries fail, the message is automatically moved to a DLQ (e.g., `basket.created.dlq`) for manual investigation.

## 6. High Concurrency & Partitioning

To handle high volumes and multiple service instances:
- **Partitioning**: Events are partitioned by `basketId`. This ensures that all events for the same basket are processed in the **exact order** they were received, even if multiple instances of a service are running.
- **Consumer Groups**: Using `group: <name>` ensures that messages are load-balanced across instances (Competing Consumer Pattern).

---
*For high-level business flows, see the [Stakeholder Guide](stakeholder-guide.md). For diagrams, see [Architecture Visuals](architecture-visuals.md).*

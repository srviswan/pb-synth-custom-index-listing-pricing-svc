package com.pb.synth.cib.infra.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;

    public <T> void publish(String bindingName, T payload) {
        String traceId = UUID.randomUUID().toString(); // In a real app, use existing OTel traceId
        EventEnvelope<T> envelope = EventEnvelope.<T>builder()
                .traceId(traceId)
                .idempotencyKey(UUID.randomUUID().toString())
                .schemaVersion("1.0")
                .occurredAt(Instant.now())
                .payload(payload)
                .build();

        log.info("Publishing event to {}: {}", bindingName, payload);
        streamBridge.send(bindingName, MessageBuilder.withPayload(envelope)
                .setHeader("partitionKey", getPartitionKey(payload))
                .build());
    }

    private Object getPartitionKey(Object payload) {
        // Try to find a basketId or similar for partitioning
        try {
            return payload.getClass().getMethod("getBasketId").invoke(payload);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}

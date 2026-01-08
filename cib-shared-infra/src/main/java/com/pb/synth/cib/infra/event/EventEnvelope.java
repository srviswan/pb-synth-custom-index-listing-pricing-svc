package com.pb.synth.cib.infra.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {
    private String traceId;
    private String idempotencyKey;
    private String schemaVersion;
    private Instant occurredAt;
    private T payload;
}

package com.pb.synth.cib.infra.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDecommissionedEvent {
    private UUID basketId;
    private String reason;
    private Instant occurredAt;
}

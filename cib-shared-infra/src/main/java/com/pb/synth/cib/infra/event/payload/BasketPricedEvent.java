package com.pb.synth.cib.infra.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketPricedEvent {
    private UUID basketId;
    private BigDecimal nav;
    private Instant asOf;
    private List<String> pricingSources;
}

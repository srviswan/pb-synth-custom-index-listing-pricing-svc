package com.pb.synth.cib.infra.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketCreateRequestedEvent {
    private String name;
    private String type;
    private String sourceSystem;
    private BigDecimal divisor;
    private List<ConstituentRequested> constituents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConstituentRequested {
        private String instrumentId;
        private String instrumentType;
        private BigDecimal weight;
        private String currency;
    }
}

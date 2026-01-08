package com.pb.synth.cib.basket.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BasketDto {
    private UUID id;
    private String name;
    private String type;
    private String status;
    private String sourceSystem;
    private Long version;
    private List<ConstituentDto> constituents;

    @Data
    @Builder
    public static class ConstituentDto {
        private String instrumentId;
        private String instrumentType;
        private BigDecimal weight;
        private BigDecimal quantity;
        private String currency;
    }
}

package com.pb.synth.cib.infra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price implements Serializable {
    private String instrumentId;
    private BigDecimal mid;
    private BigDecimal bid;
    private BigDecimal ask;
    private String currency;
    private Instant asOf;
    private String source; // BBG, REFINITIV, etc.
}

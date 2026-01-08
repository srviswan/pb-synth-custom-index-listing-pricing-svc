package com.pb.synth.cib.infra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument implements Serializable {
    private String instrumentId;
    private String ticker;
    private String name;
    private String assetClass; // EQUITY, FIXED_INCOME, etc.
    private String currency;
    private String exchange;
    private boolean active;
    private boolean eligible;
}

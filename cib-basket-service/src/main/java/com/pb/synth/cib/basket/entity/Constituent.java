package com.pb.synth.cib.basket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "constituent")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Constituent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id", nullable = false)
    private Basket basket;

    @Column(name = "instrument_id", nullable = false)
    private String instrumentId;

    @Column(name = "instrument_type", nullable = false)
    private String instrumentType;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal weight;

    @Column(precision = 18, scale = 6)
    private BigDecimal quantity;

    private String currency;

    @Column(name = "as_of")
    private Instant asOf;
}

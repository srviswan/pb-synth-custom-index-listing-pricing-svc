package com.pb.synth.cib.marketdata.service;

import com.pb.synth.cib.infra.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MarketDataService {

    private static final Duration STALENESS_THRESHOLD = Duration.ofMinutes(15);
    private final Map<String, Price> mockPriceFeed = new HashMap<>();

    public MarketDataService() {
        // Initialize with some mock data
        mockPriceFeed.put("AAPL", Price.builder()
                .instrumentId("AAPL").mid(new BigDecimal("150.00")).currency("USD")
                .asOf(Instant.now()).source("BBG").build());
        mockPriceFeed.put("MSFT", Price.builder()
                .instrumentId("MSFT").mid(new BigDecimal("300.00")).currency("USD")
                .asOf(Instant.now()).source("REFINITIV").build());
    }

    @Cacheable(value = "prices", key = "#instrumentId")
    public Optional<Price> getPrice(String instrumentId) {
        log.info("Fetching price for instrument: {}", instrumentId);
        Price price = mockPriceFeed.get(instrumentId);
        
        if (price != null && isStale(price)) {
            log.warn("Price for {} is stale (asOf: {})", instrumentId, price.getAsOf());
            // In a real system, this would trigger an async refresh or return empty
        }
        
        return Optional.ofNullable(price);
    }

    private boolean isStale(Price price) {
        return Duration.between(price.getAsOf(), Instant.now()).compareTo(STALENESS_THRESHOLD) > 0;
    }
}

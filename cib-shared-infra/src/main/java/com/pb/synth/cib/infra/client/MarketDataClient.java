package com.pb.synth.cib.infra.client;

import com.pb.synth.cib.infra.model.Price;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataClient {

    private final RestTemplate restTemplate;

    @Value("${cib.services.market-data.url:http://localhost:8084}")
    private String baseUrl;

    @CircuitBreaker(name = "marketDataService", fallbackMethod = "fallbackGetPrice")
    public Optional<Price> getPrice(String instrumentId) {
        Price price = restTemplate.getForObject(
                baseUrl + "/api/v1/marketdata/prices/" + instrumentId, 
                Price.class);
        return Optional.ofNullable(price);
    }

    public Optional<Price> fallbackGetPrice(String instrumentId, Throwable t) {
        log.warn("Fallback: MarketData Service unavailable for {}. Reason: {}", instrumentId, t.getMessage());
        return Optional.empty();
    }
}

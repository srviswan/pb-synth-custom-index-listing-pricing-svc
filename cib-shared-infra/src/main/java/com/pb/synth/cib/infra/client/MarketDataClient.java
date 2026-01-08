package com.pb.synth.cib.infra.client;

import com.pb.synth.cib.infra.model.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarketDataClient {

    private final RestTemplate restTemplate;

    @Value("${cib.services.market-data.url:http://localhost:8084}")
    private String baseUrl;

    public Optional<Price> getPrice(String instrumentId) {
        try {
            Price price = restTemplate.getForObject(
                    baseUrl + "/api/v1/marketdata/prices/" + instrumentId, 
                    Price.class);
            return Optional.ofNullable(price);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

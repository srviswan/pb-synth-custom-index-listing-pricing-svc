package com.pb.synth.cib.infra.client;

import com.pb.synth.cib.infra.model.Instrument;
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
public class ReferenceDataClient {

    private final RestTemplate restTemplate;

    @Value("${cib.services.ref-data.url:http://localhost:8083}")
    private String baseUrl;

    @CircuitBreaker(name = "refDataService", fallbackMethod = "fallbackGetInstrument")
    public Optional<Instrument> getInstrument(String instrumentId) {
        Instrument instrument = restTemplate.getForObject(
                baseUrl + "/api/v1/refdata/instruments/" + instrumentId, 
                Instrument.class);
        return Optional.ofNullable(instrument);
    }

    @CircuitBreaker(name = "refDataService", fallbackMethod = "fallbackValidateInstrument")
    public boolean validateInstrument(String instrumentId) {
        Boolean result = restTemplate.getForObject(
                baseUrl + "/api/v1/refdata/instruments/" + instrumentId + "/validate", 
                Boolean.class);
        log.info("Validation result for {}: {}", instrumentId, result);
        return Boolean.TRUE.equals(result);
    }

    public Optional<Instrument> fallbackGetInstrument(String instrumentId, Throwable t) {
        log.warn("Fallback: RefData Service unavailable for {}. Reason: {}", instrumentId, t.getMessage());
        return Optional.empty();
    }

    public boolean fallbackValidateInstrument(String instrumentId, Throwable t) {
        log.warn("Fallback: RefData Validation unavailable for {}. Reason: {}. Defaulting to true for testing.", instrumentId, t.getMessage());
        return true; 
    }
}

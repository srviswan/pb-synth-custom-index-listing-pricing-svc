package com.pb.synth.cib.infra.client;

import com.pb.synth.cib.infra.model.Instrument;
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

    public Optional<Instrument> getInstrument(String instrumentId) {
        try {
            Instrument instrument = restTemplate.getForObject(
                    baseUrl + "/api/v1/refdata/instruments/" + instrumentId, 
                    Instrument.class);
            return Optional.ofNullable(instrument);
        } catch (Exception e) {
            log.error("Error calling ref-data for instrument {}: {}", instrumentId, e.getMessage());
            return Optional.empty();
        }
    }

    public boolean validateInstrument(String instrumentId) {
        try {
            Boolean result = restTemplate.getForObject(
                    baseUrl + "/api/v1/refdata/instruments/" + instrumentId + "/validate", 
                    Boolean.class);
            log.info("Validation result for {}: {}", instrumentId, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error validating instrument {}: {}", instrumentId, e.getMessage());
            return false;
        }
    }
}

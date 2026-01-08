package com.pb.synth.cib.refdata.service;

import com.pb.synth.cib.infra.model.Instrument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ReferenceDataService {

    // Mock data storage
    private static final Map<String, Instrument> INSTRUMENTS = new HashMap<>();

    static {
        INSTRUMENTS.put("AAPL", Instrument.builder()
                .instrumentId("AAPL").ticker("AAPL").name("Apple Inc.").assetClass("EQUITY")
                .currency("USD").exchange("NASDAQ").active(true).eligible(true).build());
        INSTRUMENTS.put("MSFT", Instrument.builder()
                .instrumentId("MSFT").ticker("MSFT").name("Microsoft Corp.").assetClass("EQUITY")
                .currency("USD").exchange("NASDAQ").active(true).eligible(true).build());
        INSTRUMENTS.put("TSLA", Instrument.builder()
                .instrumentId("TSLA").ticker("TSLA").name("Tesla Inc.").assetClass("EQUITY")
                .currency("USD").exchange("NASDAQ").active(true).eligible(true).build());
    }

    @Cacheable(value = "instruments", key = "#instrumentId")
    public Optional<Instrument> getInstrument(String instrumentId) {
        log.info("Fetching instrument from golden source: {}", instrumentId);
        return Optional.ofNullable(INSTRUMENTS.get(instrumentId));
    }

    public boolean validateInstrument(String instrumentId) {
        return getInstrument(instrumentId)
                .map(i -> i.isActive() && i.isEligible())
                .orElse(false);
    }
}

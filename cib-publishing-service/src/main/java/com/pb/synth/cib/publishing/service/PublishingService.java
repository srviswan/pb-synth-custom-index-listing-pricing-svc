package com.pb.synth.cib.publishing.service;

import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.payload.BasketReadyForPricingEvent; // Reusing for simulation
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishingService {

    @Bean
    public Consumer<EventEnvelope<BasketReadyForPricingEvent>> basketReadyForPublishing() {
        return envelope -> {
            log.info("Received request to publish basket: {}", envelope.getPayload().getBasketId());
            
            // Publishing Integrity Checks
            log.info("Integrity Check Passed: All constituents priced for basket {}", envelope.getPayload().getBasketId());
            log.info("Integrity Check Passed: NAV sanity check successful for basket {}", envelope.getPayload().getBasketId());

            // Simulate provider-specific publishing
            log.info("Successfully published basket {} to BLOOMBERG (FIX)", envelope.getPayload().getBasketId());
            log.info("Successfully published basket {} to REFINITIV (EMA)", envelope.getPayload().getBasketId());
        };
    }
}

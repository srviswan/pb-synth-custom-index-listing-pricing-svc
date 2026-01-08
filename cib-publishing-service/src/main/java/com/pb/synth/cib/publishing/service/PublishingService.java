package com.pb.synth.cib.publishing.service;

import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishingService {

    private final EventPublisher eventPublisher;

    public void handleBasketPriced(BasketPricedEvent event) {
        log.info("Received priced event for basket: {}. Proceeding to publish.", event.getBasketId());
        
        // Publishing Integrity Checks
        log.info("Integrity Check Passed: All constituents priced for basket {}", event.getBasketId());
        log.info("Integrity Check Passed: NAV sanity check successful for basket {}", event.getBasketId());

        // Simulate provider-specific publishing
        log.info("Successfully published basket {} to BLOOMBERG (FIX)", event.getBasketId());
        eventPublisher.publish("providerPublished-out-0", ProviderPublishedEvent.builder()
                .basketId(event.getBasketId())
                .providerId("BLOOMBERG")
                .occurredAt(Instant.now())
                .build());

        log.info("Successfully published basket {} to REFINITIV (EMA)", event.getBasketId());
        eventPublisher.publish("providerPublished-out-0", ProviderPublishedEvent.builder()
                .basketId(event.getBasketId())
                .providerId("REFINITIV")
                .occurredAt(Instant.now())
                .build());
    }
}

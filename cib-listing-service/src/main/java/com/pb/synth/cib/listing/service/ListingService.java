package com.pb.synth.cib.listing.service;

import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketCreatedEvent;
import com.pb.synth.cib.infra.event.payload.BasketListingCompletedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderListedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderListingRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingService {

    private final EventPublisher eventPublisher;

    public void handleBasketCreated(BasketCreatedEvent event) {
        log.info("Handling basket created event for basket: {}", event.getBasketId());
        
        // Fan out to each provider
        event.getProviders().forEach(providerId -> {
            log.info("Requesting listing for provider: {} for basket: {}", providerId, event.getBasketId());
            eventPublisher.publish("providerListingRequested-out-0", ProviderListingRequestedEvent.builder()
                    .basketId(event.getBasketId())
                    .providerId(providerId)
                    .build());
            
            // Simulate async listing completion
            log.info("Successfully listed basket {} on {}", event.getBasketId(), providerId);
            eventPublisher.publish("providerListed-out-0", ProviderListedEvent.builder()
                    .basketId(event.getBasketId())
                    .providerId(providerId)
                    .occurredAt(Instant.now())
                    .build());
        });

        // After all providers are listed, emit a completion event for the next service
        log.info("All providers listed for basket: {}. Emitting completion event.", event.getBasketId());
        eventPublisher.publish("basketListingCompleted-out-0", BasketListingCompletedEvent.builder()
                .basketId(event.getBasketId())
                .occurredAt(Instant.now())
                .build());
    }
}

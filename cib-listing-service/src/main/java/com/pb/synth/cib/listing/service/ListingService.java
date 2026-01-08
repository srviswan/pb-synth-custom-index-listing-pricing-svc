package com.pb.synth.cib.listing.service;

import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketListedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderListingRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingService {

    private final EventPublisher eventPublisher;

    @Bean
    public Consumer<EventEnvelope<BasketListedEvent>> basketListed() {
        return envelope -> {
            BasketListedEvent event = envelope.getPayload();
            log.info("Received basket listed event for basket: {}", event.getBasketId());
            
            // Fan out to each provider
            event.getProviders().forEach(providerId -> {
                log.info("Requesting listing for provider: {} for basket: {}", providerId, event.getBasketId());
                eventPublisher.publish("providerListingRequested-out-0", ProviderListingRequestedEvent.builder()
                        .basketId(event.getBasketId())
                        .providerId(providerId)
                        .build());
            });
        };
    }
}

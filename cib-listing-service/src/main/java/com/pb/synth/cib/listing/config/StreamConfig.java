package com.pb.synth.cib.listing.config;

import com.pb.synth.cib.listing.service.ListingService;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.payload.BasketCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class StreamConfig {

    @Bean
    public Consumer<EventEnvelope<BasketCreatedEvent>> basketCreated(ListingService listingService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket created event for basket: {}", envelope.getPayload().getBasketId());
            listingService.handleBasketCreated(envelope.getPayload());
        };
    }
}

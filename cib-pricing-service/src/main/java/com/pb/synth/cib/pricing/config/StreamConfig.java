package com.pb.synth.cib.pricing.config;

import com.pb.synth.cib.pricing.service.PricingService;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.payload.BasketDecommissionedEvent;
import com.pb.synth.cib.infra.event.payload.BasketListingCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class StreamConfig {

    @Bean
    public Consumer<EventEnvelope<BasketListingCompletedEvent>> basketListingCompleted(PricingService pricingService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket listing completed event for basket: {}", envelope.getPayload().getBasketId());
            pricingService.handleBasketListingCompleted(envelope.getPayload());
        };
    }

    @Bean
    public Consumer<EventEnvelope<BasketDecommissionedEvent>> basketDecommissioned(PricingService pricingService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket decommissioned event for basket: {}", envelope.getPayload().getBasketId());
            pricingService.handleBasketDecommissioned(envelope.getPayload());
        };
    }
}

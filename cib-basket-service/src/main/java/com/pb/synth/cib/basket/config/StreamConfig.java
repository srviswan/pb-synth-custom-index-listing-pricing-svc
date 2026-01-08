package com.pb.synth.cib.basket.config;

import com.pb.synth.cib.basket.service.BasketService;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.payload.BasketCreateRequestedEvent;
import com.pb.synth.cib.infra.event.payload.BasketListingCompletedEvent;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderListedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderPublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class StreamConfig {

    @Bean
    public Consumer<EventEnvelope<BasketCreateRequestedEvent>> basketCreateRequested(BasketService basketService) {
        return envelope -> {
            log.info("Stream Consumer: Received async basket creation request for: {}", envelope.getPayload().getName());
            basketService.handleBasketCreateRequested(envelope.getPayload());
        };
    }

    @Bean
    public Consumer<EventEnvelope<BasketPricedEvent>> basketPriced(BasketService basketService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket priced event for basket: {}", envelope.getPayload().getBasketId());
            basketService.handleBasketPriced(envelope.getPayload());
        };
    }

    @Bean
    public Consumer<EventEnvelope<BasketListingCompletedEvent>> basketListingCompleted(BasketService basketService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket listing completed event for basket: {}", envelope.getPayload().getBasketId());
            basketService.handleBasketListingCompleted(envelope.getPayload());
        };
    }

    @Bean
    public Consumer<EventEnvelope<ProviderPublishedEvent>> providerPublished(BasketService basketService) {
        return envelope -> {
            log.info("Stream Consumer: Received provider published event for basket: {}", envelope.getPayload().getBasketId());
            basketService.handleProviderPublished(envelope.getPayload());
        };
    }
}

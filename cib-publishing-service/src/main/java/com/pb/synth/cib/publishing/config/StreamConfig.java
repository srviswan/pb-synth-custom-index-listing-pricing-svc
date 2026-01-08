package com.pb.synth.cib.publishing.config;

import com.pb.synth.cib.publishing.service.PublishingService;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class StreamConfig {

    @Bean
    public Consumer<EventEnvelope<BasketPricedEvent>> basketPriced(PublishingService publishingService) {
        return envelope -> {
            log.info("Stream Consumer: Received basket priced event for basket: {}", envelope.getPayload().getBasketId());
            publishingService.handleBasketPriced(envelope.getPayload());
        };
    }
}

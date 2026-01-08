package com.pb.synth.cib.pricing.service;

import com.pb.synth.cib.infra.client.MarketDataClient;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.event.payload.BasketReadyForPricingEvent;
import com.pb.synth.cib.infra.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private final MarketDataClient marketDataClient;
    private final EventPublisher eventPublisher;

    @Bean
    public Consumer<EventEnvelope<BasketReadyForPricingEvent>> basketReadyForPricing() {
        return envelope -> {
            BasketReadyForPricingEvent event = envelope.getPayload();
            log.info("Starting pricing for basket: {}", event.getBasketId());
            
            // In a real app, we'd fetch the basket constituents first
            // Here we'll simulate pricing for a few hardcoded symbols for the Tech Index
            List<String> symbols = List.of("AAPL", "MSFT");
            BigDecimal totalNav = BigDecimal.ZERO;
            List<String> sources = new ArrayList<>();

            for (String symbol : symbols) {
                Price price = marketDataClient.getPrice(symbol).orElse(null);
                
                // Data Quality Checks
                if (price == null) {
                    log.error("DQ Check Failed: Missing price for {}", symbol);
                    return; // Fail pricing
                }
                
                if (price.getMid().compareTo(BigDecimal.ZERO) <= 0) {
                    log.error("DQ Check Failed: Non-positive price for {}: {}", symbol, price.getMid());
                    return;
                }

                log.info("DQ Check Passed for {}: Price={}", symbol, price.getMid());
                totalNav = totalNav.add(price.getMid());
                sources.add(price.getSource());
            }

            log.info("Pricing completed for basket: {}. NAV: {}", event.getBasketId(), totalNav);
            
            eventPublisher.publish("basketPriced-out-0", BasketPricedEvent.builder()
                    .basketId(event.getBasketId())
                    .nav(totalNav)
                    .asOf(Instant.now())
                    .pricingSources(sources)
                    .build());
        };
    }
}

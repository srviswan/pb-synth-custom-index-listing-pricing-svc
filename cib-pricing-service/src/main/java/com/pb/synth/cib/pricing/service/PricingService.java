package com.pb.synth.cib.pricing.service;

import com.pb.synth.cib.infra.client.MarketDataClient;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketDecommissionedEvent;
import com.pb.synth.cib.infra.event.payload.BasketListingCompletedEvent;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private final MarketDataClient marketDataClient;
    private final EventPublisher eventPublisher;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<UUID, ScheduledFuture<?>> activePricingTasks = new ConcurrentHashMap<>();

    public void handleBasketListingCompleted(BasketListingCompletedEvent event) {
        log.info("Starting pricing for basket: {}. Initializing continuous pricing (5s interval).", event.getBasketId());
        
        // 1. Initial Pricing
        calculateAndPublish(event.getBasketId());

        // 2. Start 5-second recurring task if not already started
        activePricingTasks.computeIfAbsent(event.getBasketId(), id -> {
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
                try {
                    calculateAndPublish(id);
                } catch (Exception e) {
                    log.error("Error in continuous pricing for basket {}: {}", id, e.getMessage());
                }
            }, 5, 5, TimeUnit.SECONDS);
            log.info("Scheduled 5-second pricing heartbeat for basket: {}", id);
            return future;
        });
    }

    public void handleBasketDecommissioned(BasketDecommissionedEvent event) {
        UUID basketId = event.getBasketId();
        log.info("Stopping pricing for basket: {}. Reason: {}", basketId, event.getReason());
        
        ScheduledFuture<?> future = activePricingTasks.remove(basketId);
        if (future != null) {
            future.cancel(false);
            log.info("Successfully cancelled pricing task for basket: {}", basketId);
        } else {
            log.warn("No active pricing task found for basket: {}", basketId);
        }
    }

    private void calculateAndPublish(UUID basketId) {
        log.debug("Calculating current NAV for basket: {}", basketId);
        
        // In a real app, we'd fetch the basket constituents first
        List<String> symbols = List.of("AAPL", "MSFT");
        BigDecimal totalNav = BigDecimal.ZERO;
        List<String> sources = new ArrayList<>();

        for (String symbol : symbols) {
            Price price = marketDataClient.getPrice(symbol).orElse(null);
            
            // Data Quality Checks
            if (price == null) {
                log.error("DQ Check Failed: Missing price for {}. Skipping this update for basket {}", symbol, basketId);
                return; 
            }
            
            if (price.getMid().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("DQ Check Failed: Non-positive price for {}: {}. Skipping update for basket {}", symbol, price.getMid(), basketId);
                return;
            }

            totalNav = totalNav.add(price.getMid());
            sources.add(price.getSource());
        }

        eventPublisher.publish("basketPriced-out-0", BasketPricedEvent.builder()
                .basketId(basketId)
                .nav(totalNav)
                .asOf(Instant.now())
                .pricingSources(sources)
                .build());
        
        log.info("Continuous Pricing: Published update for basket={} NAV={} at {}", 
                basketId, totalNav, Instant.now());
    }
}

package com.pb.synth.cib.basket.service;

import com.pb.synth.cib.basket.dto.BasketDto;
import com.pb.synth.cib.basket.entity.Basket;
import com.pb.synth.cib.basket.entity.Constituent;
import com.pb.synth.cib.basket.mapper.BasketMapper;
import com.pb.synth.cib.basket.repository.BasketRepository;
import com.pb.synth.cib.infra.client.ReferenceDataClient;
import com.pb.synth.cib.infra.error.BusinessException;
import com.pb.synth.cib.infra.event.EventEnvelope;
import com.pb.synth.cib.infra.event.EventPublisher;
import com.pb.synth.cib.infra.event.payload.BasketListedEvent;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.event.payload.BasketReadyForPricingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;
    private final ReferenceDataClient referenceDataClient;
    private final EventPublisher eventPublisher;

    @Bean
    public Consumer<EventEnvelope<BasketPricedEvent>> basketPriced() {
        return envelope -> {
            BasketPricedEvent event = envelope.getPayload();
            log.info("Received basket priced event for basket: {}. NAV: {}", event.getBasketId(), event.getNav());
            updateStatus(event.getBasketId(), "PRICED");
        };
    }

    private void updateStatus(UUID basketId, String status) {
        basketRepository.findById(basketId).ifPresent(basket -> {
            log.info("Updating basket {} status to {}", basketId, status);
            basket.setStatus(status);
            basketRepository.save(basket);
        });
    }

    @Transactional
    public BasketDto createBasket(BasketDto basketDto) {
        log.info("Creating new basket: {}", basketDto.getName());
        Basket basket = basketMapper.toEntity(basketDto);
        basket.setStatus("DRAFT");
        
        if (basketDto.getConstituents() != null) {
            basketDto.getConstituents().forEach(cDto -> {
                validateConstituent(cDto.getInstrumentId());
                Constituent constituent = basketMapper.toEntity(cDto);
                basket.addConstituent(constituent);
            });
        }

        Basket saved = basketRepository.save(basket);
        return basketMapper.toDto(saved);
    }

    @Transactional
    public BasketDto updateConstituents(UUID basketId, List<BasketDto.ConstituentDto> constituentDtos) {
        log.info("Updating constituents for basket: {}", basketId);
        
        // Use forced increment to ensure version change on children update
        Basket basket = basketRepository.findByIdWithForceIncrement(basketId)
                .orElseThrow(() -> new BusinessException("BASKET_NOT_FOUND", "Basket not found", HttpStatus.NOT_FOUND));

        if (!"DRAFT".equals(basket.getStatus())) {
            throw new BusinessException("INVALID_STATUS", "Constituents can only be updated in DRAFT status", HttpStatus.BAD_REQUEST);
        }

        // Clear existing and add new
        basket.getConstituents().clear();
        constituentDtos.forEach(cDto -> {
            validateConstituent(cDto.getInstrumentId());
            Constituent constituent = basketMapper.toEntity(cDto);
            basket.addConstituent(constituent);
        });

        Basket saved = basketRepository.save(basket);
        return basketMapper.toDto(saved);
    }

    private void validateConstituent(String instrumentId) {
        if (!referenceDataClient.validateInstrument(instrumentId)) {
            throw new BusinessException("INVALID_INSTRUMENT", 
                    "Instrument " + instrumentId + " is invalid or ineligible", 
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public BasketDto getBasket(UUID id) {
        return basketRepository.findById(id)
                .map(basketMapper::toDto)
                .orElseThrow(() -> new BusinessException("BASKET_NOT_FOUND", "Basket not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void markReadyForListing(UUID basketId) {
        log.info("Marking basket {} ready for listing", basketId);
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BusinessException("BASKET_NOT_FOUND", "Basket not found", HttpStatus.NOT_FOUND));

        if (!"DRAFT".equals(basket.getStatus())) {
            throw new BusinessException("INVALID_STATUS", "Only DRAFT baskets can be listed", HttpStatus.BAD_REQUEST);
        }

        basket.setStatus("LISTED");
        basketRepository.save(basket);

        eventPublisher.publish("basketListed-out-0", BasketListedEvent.builder()
                .basketId(basketId)
                .providers(List.of("BLOOMBERG", "REFINITIV"))
                .build());
    }

    @Transactional
    public void markReadyForPricing(UUID basketId) {
        log.info("Marking basket {} ready for pricing", basketId);
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BusinessException("BASKET_NOT_FOUND", "Basket not found", HttpStatus.NOT_FOUND));

        if (!"LISTED".equals(basket.getStatus())) {
            throw new BusinessException("INVALID_STATUS", "Only LISTED baskets can be marked for pricing", HttpStatus.BAD_REQUEST);
        }

        basket.setStatus("READY_FOR_PRICING");
        basketRepository.save(basket);

        eventPublisher.publish("basketReadyForPricing-out-0", BasketReadyForPricingEvent.builder()
                .basketId(basketId)
                .build());
    }

    @Transactional
    public void markReadyForPublishing(UUID basketId) {
        log.info("Marking basket {} ready for publishing", basketId);
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BusinessException("BASKET_NOT_FOUND", "Basket not found", HttpStatus.NOT_FOUND));

        if (!"PRICED".equals(basket.getStatus())) {
            throw new BusinessException("INVALID_STATUS", "Only PRICED baskets can be marked for publishing", HttpStatus.BAD_REQUEST);
        }

        basket.setStatus("READY_FOR_PUBLISHING");
        basketRepository.save(basket);

        // No explicit event here, assumed Publishing Service will listen to status changes or we emit another event
        // Let's emit a ReadyForPublishing event
        eventPublisher.publish("basketReadyForPublishing-out-0", BasketReadyForPricingEvent.builder() // Reuse payload for now
                .basketId(basketId)
                .build());
    }
}

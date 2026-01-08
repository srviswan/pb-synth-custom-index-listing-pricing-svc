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
import com.pb.synth.cib.infra.event.payload.BasketCreateRequestedEvent;
import com.pb.synth.cib.infra.event.payload.BasketCreatedEvent;
import com.pb.synth.cib.infra.event.payload.BasketListingCompletedEvent;
import com.pb.synth.cib.infra.event.payload.BasketPricedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderListedEvent;
import com.pb.synth.cib.infra.event.payload.ProviderPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;
    private final ReferenceDataClient referenceDataClient;
    private final EventPublisher eventPublisher;

    @Transactional
    public void handleBasketCreateRequested(BasketCreateRequestedEvent event) {
        log.info("Handling async basket creation request for: {}", event.getName());
        BasketDto dto = BasketDto.builder()
                .name(event.getName())
                .type(event.getType())
                .sourceSystem(event.getSourceSystem())
                .divisor(event.getDivisor())
                .constituents(event.getConstituents().stream()
                        .map(c -> BasketDto.ConstituentDto.builder()
                                .instrumentId(c.getInstrumentId())
                                .instrumentType(c.getInstrumentType())
                                .weight(c.getWeight())
                                .currency(c.getCurrency())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        createBasket(dto);
    }

    @Transactional
    public void handleBasketPriced(BasketPricedEvent event) {
        log.info("Handling basket priced event for basket: {}. NAV: {}", event.getBasketId(), event.getNav());
        updateStatus(event.getBasketId(), "PRICED");
    }

    @Transactional
    public void handleBasketListingCompleted(BasketListingCompletedEvent event) {
        log.info("Handling basket listing completed event for basket: {}", event.getBasketId());
        updateStatus(event.getBasketId(), "LISTED");
    }

    @Transactional
    public void handleProviderPublished(ProviderPublishedEvent event) {
        log.info("Handling provider published event for basket: {}", event.getBasketId());
        updateStatus(event.getBasketId(), "PUBLISHED");
    }

    @Transactional
    public void updateStatus(UUID basketId, String status) {
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
        basket.setStatus("LISTING_IN_PROGRESS");
        
        if (basketDto.getConstituents() != null) {
            basketDto.getConstituents().forEach(cDto -> {
                validateConstituent(cDto.getInstrumentId());
                Constituent constituent = basketMapper.toEntity(cDto);
                basket.addConstituent(constituent);
            });
        }

        Basket saved = basketRepository.save(basket);

        // Choreography: Emit the first event to kick off the chain
        eventPublisher.publish("basketCreated-out-0", BasketCreatedEvent.builder()
                .basketId(saved.getId())
                .providers(List.of("BLOOMBERG", "REFINITIV"))
                .build());

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
}

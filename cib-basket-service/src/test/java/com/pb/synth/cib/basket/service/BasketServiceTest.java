package com.pb.synth.cib.basket.service;

import com.pb.synth.cib.basket.dto.BasketDto;
import com.pb.synth.cib.basket.entity.Basket;
import com.pb.synth.cib.basket.entity.Constituent;
import com.pb.synth.cib.basket.mapper.BasketMapper;
import com.pb.synth.cib.basket.repository.BasketRepository;
import com.pb.synth.cib.infra.client.ReferenceDataClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketMapper basketMapper;

    @Mock
    private ReferenceDataClient referenceDataClient;

    @Mock
    private com.pb.synth.cib.infra.event.EventPublisher eventPublisher;

    @InjectMocks
    private BasketService basketService;

    private UUID basketId;
    private Basket basket;
    private BasketDto basketDto;

    @BeforeEach
    void setUp() {
        basketId = UUID.randomUUID();
        basket = Basket.builder()
                .id(basketId)
                .name("Test Basket")
                .status("DRAFT")
                .divisor(new BigDecimal("100.0"))
                .constituents(new ArrayList<>())
                .version(1L)
                .build();

        basketDto = BasketDto.builder()
                .name("Test Basket")
                .divisor(new BigDecimal("100.0"))
                .constituents(List.of(
                        BasketDto.ConstituentDto.builder()
                                .instrumentId("AAPL")
                                .weight(new BigDecimal("0.5"))
                                .build()
                ))
                .build();
    }

    @Test
    void createBasket_ShouldSaveAndReturnDto() {
        when(referenceDataClient.validateInstrument(any())).thenReturn(true);
        when(basketMapper.toEntity(any(BasketDto.class))).thenReturn(basket);
        when(basketMapper.toEntity(any(BasketDto.ConstituentDto.class))).thenReturn(new Constituent());
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(basketMapper.toDto(any(Basket.class))).thenReturn(basketDto);

        BasketDto result = basketService.createBasket(basketDto);

        assertNotNull(result);
        verify(basketRepository).save(any(Basket.class));
        assertEquals("LISTING_IN_PROGRESS", basket.getStatus());
        assertEquals(new BigDecimal("100.0"), basket.getDivisor());
    }

    @Test
    void updateConstituents_ShouldForceVersionIncrement() {
        List<BasketDto.ConstituentDto> newConstituents = List.of(
                BasketDto.ConstituentDto.builder().instrumentId("MSFT").weight(new BigDecimal("1.0")).build()
        );

        when(referenceDataClient.validateInstrument(any())).thenReturn(true);
        when(basketRepository.findByIdWithForceIncrement(basketId)).thenReturn(Optional.of(basket));
        when(basketMapper.toEntity(any(BasketDto.ConstituentDto.class))).thenReturn(new Constituent());
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(basketMapper.toDto(any(Basket.class))).thenReturn(basketDto);

        basketService.updateConstituents(basketId, newConstituents);

        verify(basketRepository).findByIdWithForceIncrement(basketId);
        verify(basketRepository).save(basket);
    }
}

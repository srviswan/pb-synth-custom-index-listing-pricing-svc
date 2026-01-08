package com.pb.synth.cib.basket.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.synth.cib.basket.dto.BasketDto;
import com.pb.synth.cib.basket.service.BasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketService basketService;

    @MockBean
    private com.pb.synth.cib.infra.event.EventPublisher eventPublisher;

    @MockBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBasket_ShouldReturnCreated() throws Exception {
        BasketDto request = BasketDto.builder()
                .name("Test Basket")
                .type("EQUITY")
                .sourceSystem("FRONT_OFFICE")
                .divisor(new BigDecimal("100.0"))
                .build();

        BasketDto response = BasketDto.builder()
                .id(UUID.randomUUID())
                .name("Test Basket")
                .status("DRAFT")
                .divisor(new BigDecimal("100.0"))
                .version(1L)
                .build();

        when(basketService.createBasket(any(BasketDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/baskets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Basket"))
                .andExpect(jsonPath("$.divisor").value(100.0))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getBasket_ShouldReturnBasket() throws Exception {
        UUID id = UUID.randomUUID();
        BasketDto response = BasketDto.builder()
                .id(id)
                .name("Existing Basket")
                .status("DRAFT")
                .build();

        when(basketService.getBasket(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/baskets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Basket"));
    }

    @Test
    void updateConstituents_ShouldReturnUpdatedBasket() throws Exception {
        UUID id = UUID.randomUUID();
        List<BasketDto.ConstituentDto> constituents = List.of(
                BasketDto.ConstituentDto.builder()
                        .instrumentId("AAPL")
                        .instrumentType("STOCK")
                        .weight(new BigDecimal("1.0"))
                        .build()
        );

        BasketDto response = BasketDto.builder()
                .id(id)
                .name("Updated Basket")
                .constituents(constituents)
                .build();

        when(basketService.updateConstituents(eq(id), anyList())).thenReturn(response);

        mockMvc.perform(put("/api/v1/baskets/{id}/constituents", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(constituents)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constituents[0].instrumentId").value("AAPL"));
    }
}

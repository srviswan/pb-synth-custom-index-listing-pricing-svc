package com.pb.synth.cib.basket.web;

import com.pb.synth.cib.basket.dto.BasketDto;
import com.pb.synth.cib.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BasketDto createBasket(@RequestBody BasketDto basketDto) {
        return basketService.createBasket(basketDto);
    }

    @GetMapping("/{id}")
    public BasketDto getBasket(@PathVariable UUID id) {
        return basketService.getBasket(id);
    }

    @PutMapping("/{id}/constituents")
    public BasketDto updateConstituents(@PathVariable UUID id, @RequestBody List<BasketDto.ConstituentDto> constituents) {
        return basketService.updateConstituents(id, constituents);
    }
}

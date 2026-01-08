package com.pb.synth.cib.marketdata.web;

import com.pb.synth.cib.infra.model.Price;
import com.pb.synth.cib.marketdata.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketdata")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @GetMapping("/prices/{instrumentId}")
    public ResponseEntity<Price> getPrice(@PathVariable String instrumentId) {
        return marketDataService.getPrice(instrumentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

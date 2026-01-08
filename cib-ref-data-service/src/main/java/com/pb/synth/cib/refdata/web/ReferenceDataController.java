package com.pb.synth.cib.refdata.web;

import com.pb.synth.cib.infra.model.Instrument;
import com.pb.synth.cib.refdata.service.ReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/refdata")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    @GetMapping("/instruments/{id}")
    public ResponseEntity<Instrument> getInstrument(@PathVariable String id) {
        return referenceDataService.getInstrument(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instruments/{id}/validate")
    public ResponseEntity<Boolean> validateInstrument(@PathVariable String id) {
        return ResponseEntity.ok(referenceDataService.validateInstrument(id));
    }
}

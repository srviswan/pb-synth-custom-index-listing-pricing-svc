package com.pb.synth.cib.infra.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderListingRequestedEvent {
    private UUID basketId;
    private String providerId;
}

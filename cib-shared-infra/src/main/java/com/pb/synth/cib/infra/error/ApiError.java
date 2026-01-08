package com.pb.synth.cib.infra.error;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ApiError {
    private String code;
    private String message;
    private String details;
    private String traceId;
    private Instant timestamp;
    private List<ValidationError> validationErrors;

    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String reason;
        private String instrumentId;
    }
}

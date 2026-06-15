package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "tableId is required")
    private Long tableId;
    private Long customerId;
    private String customerName;
    private String phone;
    @Positive(message = "partySize must be positive")
    private Integer partySize;
    @NotNull(message = "reservedFrom is required")
    private LocalDateTime reservedFrom;
    private LocalDateTime reservedTo;
    private String note;
}

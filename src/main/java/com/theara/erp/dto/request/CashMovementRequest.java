package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CashMovementRequest {
    @NotNull(message = "direction is required")
    @Pattern(regexp = "IN|OUT", message = "direction must be IN or OUT")
    private String direction;
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;
    private String reason;
    private String referenceType;
    private Long referenceId;
    private Long createdBy;
}

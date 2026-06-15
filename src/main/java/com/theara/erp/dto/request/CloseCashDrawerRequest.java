package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CloseCashDrawerRequest {
    private Long closedBy;
    @NotNull(message = "countedBalance is required")
    @PositiveOrZero(message = "countedBalance must be zero or positive")
    private BigDecimal countedBalance;
}

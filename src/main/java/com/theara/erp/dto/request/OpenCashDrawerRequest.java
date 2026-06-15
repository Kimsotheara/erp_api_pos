package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OpenCashDrawerRequest {
    @NotNull(message = "branchId is required")
    private Long branchId;
    private Long openedBy;
    @PositiveOrZero(message = "openingBalance must be zero or positive")
    private BigDecimal openingBalance;
}

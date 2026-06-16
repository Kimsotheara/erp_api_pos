package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockAdjustRequest {
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "countedQuantity is required")
    @PositiveOrZero(message = "countedQuantity must be >= 0")
    private BigDecimal countedQuantity;
    private String note;
}

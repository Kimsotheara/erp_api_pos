package com.theara.erp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockOutRequest {
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.001", message = "quantity must be > 0")
    private BigDecimal quantity;
    private String note;
}

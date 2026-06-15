package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderItemRequest {
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be positive")
    private BigDecimal quantity;
    @NotNull(message = "unitCost is required")
    @PositiveOrZero(message = "unitCost must be zero or positive")
    private BigDecimal unitCost;
}

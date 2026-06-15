package com.theara.erp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.001", message = "quantity must be > 0")
    private BigDecimal quantity;

    /** Optional. If omitted, the active RETAIL product price is used. */
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "discountAmount must be >= 0")
    private BigDecimal discountAmount;
}

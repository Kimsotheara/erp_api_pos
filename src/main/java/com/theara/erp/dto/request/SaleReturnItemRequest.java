package com.theara.erp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SaleReturnItemRequest {
    @NotNull(message = "invoiceItemId is required")
    private Long invoiceItemId;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.001", message = "quantity must be > 0")
    private BigDecimal quantity;
}

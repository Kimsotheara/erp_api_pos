package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SettleBillRequest {
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    private BigDecimal discountAmount;
    @NotEmpty(message = "at least one payment is required")
    @Valid
    private List<SalePaymentRequest> payments;
}

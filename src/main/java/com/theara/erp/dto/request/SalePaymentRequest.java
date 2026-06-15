package com.theara.erp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalePaymentRequest {
    @NotNull(message = "paymentMethodId is required")
    private Long paymentMethodId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be > 0")
    private BigDecimal amount;

    private String referenceNo;
}

package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;

    @NotNull(message = "branchId is required")
    private Long branchId;

    @NotNull(message = "warehouseId is required")
    private Long warehouseId;

    private Long customerId;

    private Long cashierId;

    private BigDecimal discountAmount;

    private String note;

    @Valid
    @NotEmpty(message = "At least one item is required")
    private List<SaleItemRequest> items;

    @Valid
    @NotEmpty(message = "At least one payment is required")
    private List<SalePaymentRequest> payments;
}

package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotNull(message = "supplierId is required")
    private Long supplierId;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private String note;
    private Long requestedBy;
    @NotEmpty(message = "at least one item is required")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}

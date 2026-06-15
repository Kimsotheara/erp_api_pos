package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchTransferRequest {
    @NotNull(message = "fromBranchId is required")
    private Long fromBranchId;
    @NotNull(message = "toBranchId is required")
    private Long toBranchId;
    @NotNull(message = "fromWarehouseId is required")
    private Long fromWarehouseId;
    @NotNull(message = "toWarehouseId is required")
    private Long toWarehouseId;
    private LocalDate transferDate;
    private String note;
    private Long createdBy;
    @NotEmpty(message = "at least one item is required")
    @Valid
    private List<BranchTransferItemRequest> items;
}

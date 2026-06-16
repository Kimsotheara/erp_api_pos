package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SaleReturnRequest {
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;

    private String reason;

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<SaleReturnItemRequest> items;
}

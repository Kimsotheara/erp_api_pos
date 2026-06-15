package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VoidInvoiceRequest {
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    private String reason;
}

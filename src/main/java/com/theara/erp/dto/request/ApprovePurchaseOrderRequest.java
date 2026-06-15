package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApprovePurchaseOrderRequest {
    @NotNull(message = "approvedBy is required")
    private Long approvedBy;

    private Boolean approve;
    private String note;
}

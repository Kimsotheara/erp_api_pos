package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApprovePurchaseOrderRequest {
    @NotNull(message = "approvedBy is required")
    private Long approvedBy;
    /** When false, the PO is rejected instead of approved. */
    private Boolean approve;
    private String note;
}

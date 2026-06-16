package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OpenTableRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotNull(message = "tableId is required")
    private Long tableId;
    private Long customerId;
    private Long cashierId;
}

package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WarehouseRequest {
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotBlank(message = "code is required")
    private String code;
    @NotBlank(message = "name is required")
    private String name;
    private Boolean isDefault;
    private Boolean isActive;
}

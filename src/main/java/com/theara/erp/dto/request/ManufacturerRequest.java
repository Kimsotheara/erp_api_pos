package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ManufacturerRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "name is required")
    private String name;
    private String country;
    private Boolean isActive;
}

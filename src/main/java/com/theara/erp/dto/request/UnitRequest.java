package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UnitRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "name is required")
    private String name;
    private String abbreviation;
    private Boolean isActive;
}

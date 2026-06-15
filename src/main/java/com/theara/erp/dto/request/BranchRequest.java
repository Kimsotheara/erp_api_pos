package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "code is required")
    private String code;
    @NotBlank(message = "name is required")
    private String name;
    private String phone;
    private String address;
    private Long currencyId;
    private Boolean isActive;
}

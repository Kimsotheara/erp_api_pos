package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentMethodRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "name is required")
    private String name;
    private Boolean isCash;
    private Boolean isActive;
}

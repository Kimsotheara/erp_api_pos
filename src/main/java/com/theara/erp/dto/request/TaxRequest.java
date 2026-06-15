package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaxRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "rate is required")
    @PositiveOrZero(message = "rate must be zero or positive")
    private BigDecimal rate;
    private Boolean isInclusive;
    private Boolean isActive;
}

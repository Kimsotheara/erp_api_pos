package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CurrencyRequest {
    @NotBlank(message = "code is required")
    @Size(min = 3, max = 3, message = "code must be a 3-letter ISO 4217 code")
    private String code;
    @NotBlank(message = "name is required")
    private String name;
    private String symbol;
    private BigDecimal exchangeRate;
    private Boolean isBase;
    private Boolean isActive;
}

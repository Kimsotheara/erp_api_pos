package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IncomeRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    private Long branchId;
    private String category;
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;
    private String description;
    private LocalDate incomeDate;
    private Long createdBy;
}

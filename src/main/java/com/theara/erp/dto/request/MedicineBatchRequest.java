package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicineBatchRequest {
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    private Long manufacturerId;
    private Long drugCategoryId;
    @NotBlank(message = "batchNumber is required")
    private String batchNumber;
    private LocalDate manufactureDate;
    @NotNull(message = "expiryDate is required")
    private LocalDate expiryDate;
    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be positive")
    private BigDecimal quantity;
    @PositiveOrZero(message = "costPrice must be zero or positive")
    private BigDecimal costPrice;
}

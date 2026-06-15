package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicineBatchResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private Long manufacturerId;
    private String manufacturerName;
    private Long drugCategoryId;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private LocalDateTime createdAt;
}

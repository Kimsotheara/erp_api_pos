package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LowStockResponse {
    private Long productId;
    private String productName;
    private BigDecimal reorderLevel;
    private BigDecimal onHand;
}

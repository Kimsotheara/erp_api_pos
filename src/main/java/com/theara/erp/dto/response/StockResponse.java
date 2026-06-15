package com.theara.erp.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockResponse {
    private Long warehouseId;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal avgCost;
}

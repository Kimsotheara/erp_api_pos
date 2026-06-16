package com.theara.erp.dto.response;

import com.theara.erp.constant.StockMovementType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockMovementResponse {
    private Long id;
    private Long warehouseId;
    private Long productId;
    private String productName;
    private StockMovementType movementType;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private String referenceType;
    private Long referenceId;
    private String note;
    private LocalDateTime createdAt;
}

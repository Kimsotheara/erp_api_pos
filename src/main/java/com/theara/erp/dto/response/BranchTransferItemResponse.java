package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchTransferItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
}

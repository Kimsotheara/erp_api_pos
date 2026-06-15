package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoodsReceiptResponse {
    private Long id;
    private Long purchaseOrderId;
    private Long warehouseId;
    private String grnNumber;
    private LocalDate receivedDate;
    private Long receivedBy;
    private String note;
    private List<GoodsReceiptItemResponse> items;
    private LocalDateTime createdAt;
}

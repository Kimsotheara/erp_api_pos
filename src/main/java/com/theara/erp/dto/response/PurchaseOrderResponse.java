package com.theara.erp.dto.response;

import com.theara.erp.constant.PoStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderResponse {
    private Long id;
    private Long companyId;
    private Long branchId;
    private Long supplierId;
    private String supplierName;
    private String poNumber;
    private PoStatus status;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String note;
    private Long requestedBy;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private List<PurchaseOrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

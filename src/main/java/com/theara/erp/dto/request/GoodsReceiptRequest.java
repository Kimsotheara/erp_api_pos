package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoodsReceiptRequest {
    /** Optional — a receipt may be against a PO or a direct (ad-hoc) receipt. */
    private Long purchaseOrderId;
    @NotNull(message = "warehouseId is required")
    private Long warehouseId;
    private LocalDate receivedDate;
    private Long receivedBy;
    private String note;
    @NotEmpty(message = "at least one item is required")
    @Valid
    private List<GoodsReceiptItemRequest> items;
}

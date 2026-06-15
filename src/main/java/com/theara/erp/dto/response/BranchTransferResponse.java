package com.theara.erp.dto.response;

import com.theara.erp.constant.TransferStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchTransferResponse {
    private Long id;
    private String transferNo;
    private Long fromBranchId;
    private Long toBranchId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private TransferStatus status;
    private LocalDate transferDate;
    private LocalDate receivedDate;
    private String note;
    private Long createdBy;
    private List<BranchTransferItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

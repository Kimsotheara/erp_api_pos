package com.theara.erp.dto.response;

import com.theara.erp.constant.CashDrawerStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CashDrawerResponse {
    private Long id;
    private Long branchId;
    private Long openedBy;
    private Long closedBy;
    private CashDrawerStatus status;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal expectedBalance;
    private BigDecimal difference;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private List<CashMovementResponse> movements;
}

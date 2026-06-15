package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CashMovementResponse {
    private Long id;
    private String direction;
    private BigDecimal amount;
    private String reason;
    private String referenceType;
    private Long referenceId;
    private Long createdBy;
    private LocalDateTime createdAt;
}

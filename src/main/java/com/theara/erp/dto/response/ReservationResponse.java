package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long tableId;
    private String tableName;
    private Long customerId;
    private String customerName;
    private String phone;
    private Integer partySize;
    private LocalDateTime reservedFrom;
    private LocalDateTime reservedTo;
    private String note;
    private LocalDateTime createdAt;
}

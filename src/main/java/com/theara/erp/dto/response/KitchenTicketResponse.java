package com.theara.erp.dto.response;

import com.theara.erp.constant.KitchenStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenTicketResponse {
    private Long id;
    private Long invoiceId;
    private Long tableId;
    private String ticketNumber;
    private KitchenStatus status;
    private List<KitchenTicketItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime servedAt;
}

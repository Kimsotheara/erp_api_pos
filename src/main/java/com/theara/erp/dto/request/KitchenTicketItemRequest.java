package com.theara.erp.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenTicketItemRequest {
    private Long menuItemId;
    @Positive(message = "quantity must be positive")
    private BigDecimal quantity;
    private String note;
}

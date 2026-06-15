package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenTicketRequest {
    private Long invoiceId;
    private Long tableId;
    @NotEmpty(message = "at least one item is required")
    @Valid
    private List<KitchenTicketItemRequest> items;
}

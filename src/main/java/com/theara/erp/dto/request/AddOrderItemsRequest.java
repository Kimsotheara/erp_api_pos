package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddOrderItemsRequest {
    private Boolean happyHour;
    @NotEmpty(message = "at least one item is required")
    @Valid
    private List<OrderLineRequest> items;
}

package com.theara.erp.dto.request;

import com.theara.erp.constant.KitchenStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenStatusRequest {
    @NotNull(message = "status is required")
    private KitchenStatus status;
}

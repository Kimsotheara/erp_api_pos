package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ActiveStatusRequest {
    @NotNull(message = "isActive is required")
    private Boolean isActive;
}

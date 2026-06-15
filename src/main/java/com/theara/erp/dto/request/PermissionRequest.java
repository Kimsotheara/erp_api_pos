package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "code is required")
    private String code;
    private String description;
}

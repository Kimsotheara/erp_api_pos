package com.theara.erp.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermissionResponse {
    private Long id;
    private String code;
    private String description;
}

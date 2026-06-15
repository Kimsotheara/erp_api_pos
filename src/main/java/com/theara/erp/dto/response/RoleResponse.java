package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleResponse {
    private Long id;
    private Long companyId;
    private String name;
    private String description;
    private Boolean isSystem;
    private List<PermissionResponse> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

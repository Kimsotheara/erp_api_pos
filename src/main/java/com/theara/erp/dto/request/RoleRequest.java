package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "name is required")
    private String name;
    private String description;
    private Boolean isSystem;
    /** Permission IDs granted to this role. */
    private List<Long> permissionIds;
}

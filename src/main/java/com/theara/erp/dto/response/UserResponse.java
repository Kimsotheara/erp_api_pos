package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private Long companyId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Long defaultBranchId;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private List<RoleSummaryResponse> roles;
    private List<Long> branchIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

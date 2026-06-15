package com.theara.erp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "username is required")
    private String username;
    @Email(message = "Email must be valid")
    private String email;
    /** Required on create; optional on update (blank = keep existing password). */
    private String password;
    private String fullName;
    private String phone;
    /** Base64-encoded staff photo (data URI or raw base64). */
    private String image;
    private Long defaultBranchId;
    private Boolean isActive;
    private List<Long> roleIds;
    private List<Long> branchIds;
}

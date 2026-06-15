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

    private String password;
    private String fullName;
    private String phone;

    private String image;
    private Long defaultBranchId;
    private Boolean isActive;
    private List<Long> roleIds;
    private List<Long> branchIds;
}

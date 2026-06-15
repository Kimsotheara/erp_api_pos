package com.theara.erp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    private String code;
    @NotBlank(message = "name is required")
    private String name;
    private String phone;
    @Email(message = "Email must be valid")
    private String email;
    private String address;

    private String image;
    private String membershipNo;
    private String membershipTier;
    private Integer loyaltyBalance;
    private Boolean isActive;
}

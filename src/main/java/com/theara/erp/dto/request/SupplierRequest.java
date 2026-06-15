package com.theara.erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    private String code;
    @NotBlank(message = "name is required")
    private String name;
    private String taxNumber;
    private String phone;
    @Email(message = "Email must be valid")
    private String email;
    private String address;
    private Boolean isActive;
    @Valid
    private List<SupplierContactRequest> contacts;
}

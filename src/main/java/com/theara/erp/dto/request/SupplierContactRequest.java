package com.theara.erp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierContactRequest {
    @NotBlank(message = "contact name is required")
    private String name;
    private String position;
    private String phone;
    @Email(message = "Email must be valid")
    private String email;
}

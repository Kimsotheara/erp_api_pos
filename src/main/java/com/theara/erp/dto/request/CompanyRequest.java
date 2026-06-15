package com.theara.erp.dto.request;

import com.theara.erp.constant.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    private String legalName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    private String taxNumber;

    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    private String address;

    private String logoUrl;

    /** Base64-encoded image (data URI or raw base64). */
    private String image;

    private Boolean isActive;
}

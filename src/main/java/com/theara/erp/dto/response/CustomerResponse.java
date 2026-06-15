package com.theara.erp.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private Long companyId;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String image;
    private String membershipNo;
    private String membershipTier;
    private Integer loyaltyBalance;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

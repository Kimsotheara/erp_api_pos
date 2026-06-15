package com.theara.erp.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BranchResponse {
    private Long id;
    private Long companyId;
    private String code;
    private String name;
    private String phone;
    private String address;
    private Long currencyId;
    private String currencyCode;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

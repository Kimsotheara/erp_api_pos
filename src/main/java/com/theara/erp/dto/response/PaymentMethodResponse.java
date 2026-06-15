package com.theara.erp.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentMethodResponse {
    private Long id;
    private Long companyId;
    private String name;
    private Boolean isCash;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

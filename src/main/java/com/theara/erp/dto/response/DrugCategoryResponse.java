package com.theara.erp.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DrugCategoryResponse {
    private Long id;
    private Long companyId;
    private String name;
    private Boolean requiresPrescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

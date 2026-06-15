package com.theara.erp.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleSummaryResponse {
    private Long id;
    private String name;
}

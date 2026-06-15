package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExpenseCategoryResponse {
    private String category;
    private BigDecimal total;
    private Long count;
}

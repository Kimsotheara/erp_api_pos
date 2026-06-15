package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuItemResponse implements Imageable {
    private Long id;
    private Long companyId;
    private Long productId;
    private String name;
    private String image;
    private String category;
    private BigDecimal price;
    private BigDecimal happyHourPrice;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

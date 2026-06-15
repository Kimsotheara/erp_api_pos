package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuItemRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    private Long productId;
    @NotBlank(message = "name is required")
    private String name;
    /** Base64-encoded menu-item photo (data URI or raw base64). */
    private String image;
    private String category;
    @NotNull(message = "price is required")
    @PositiveOrZero(message = "price must be zero or positive")
    private BigDecimal price;
    @PositiveOrZero(message = "happyHourPrice must be zero or positive")
    private BigDecimal happyHourPrice;
    private Boolean isAvailable;
}

package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;

    @NotBlank(message = "SKU is required")
    @Size(max = 60)
    private String sku;

    private String barcode;

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    private String description;

    private String image;

    private Long categoryId;
    private Long brandId;
    private Long unitId;
    private Long taxId;

    @PositiveOrZero(message = "costPrice must be >= 0")
    private BigDecimal costPrice;

    private Boolean isService;
    private Boolean trackStock;

    @PositiveOrZero(message = "reorderLevel must be >= 0")
    private BigDecimal reorderLevel;

    private Boolean isActive;
}

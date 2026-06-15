package com.theara.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Imageable {
    private Long id;
    private Long companyId;
    private String sku;
    private String barcode;
    private String name;
    private String description;
    private String image;

    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Long unitId;
    private String unitName;
    private Long taxId;
    private String taxName;

    private BigDecimal costPrice;
    private Boolean isService;
    private Boolean trackStock;
    private BigDecimal reorderLevel;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

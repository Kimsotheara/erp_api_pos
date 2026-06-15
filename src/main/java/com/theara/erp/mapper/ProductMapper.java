package com.theara.erp.mapper;

import com.theara.erp.dto.response.ProductResponse;
import com.theara.erp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Entity -> Response only. Request -> Entity is handled in the service
 * because foreign keys (category/brand/unit/tax) must be resolved to managed
 * entities first.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "taxId", source = "tax.id")
    @Mapping(target = "taxName", source = "tax.name")
    ProductResponse toResponse(Product product);
}

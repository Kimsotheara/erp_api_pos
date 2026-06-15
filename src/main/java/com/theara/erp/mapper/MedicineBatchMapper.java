package com.theara.erp.mapper;

import com.theara.erp.dto.response.MedicineBatchResponse;
import com.theara.erp.entity.MedicineBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicineBatchMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "manufacturerId", source = "manufacturer.id")
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "drugCategoryId", source = "drugCategory.id")
    MedicineBatchResponse toResponse(MedicineBatch batch);
}

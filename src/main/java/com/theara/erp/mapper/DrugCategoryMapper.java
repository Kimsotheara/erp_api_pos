package com.theara.erp.mapper;

import com.theara.erp.dto.response.DrugCategoryResponse;
import com.theara.erp.entity.DrugCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DrugCategoryMapper {
    @Mapping(target = "companyId", source = "company.id")
    DrugCategoryResponse toResponse(DrugCategory drugCategory);
}

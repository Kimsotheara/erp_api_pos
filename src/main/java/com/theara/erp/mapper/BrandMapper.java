package com.theara.erp.mapper;

import com.theara.erp.dto.response.BrandResponse;
import com.theara.erp.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    @Mapping(target = "companyId", source = "company.id")
    BrandResponse toResponse(Brand brand);
}

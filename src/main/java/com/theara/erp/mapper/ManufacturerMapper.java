package com.theara.erp.mapper;

import com.theara.erp.dto.response.ManufacturerResponse;
import com.theara.erp.entity.Manufacturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {
    @Mapping(target = "companyId", source = "company.id")
    ManufacturerResponse toResponse(Manufacturer manufacturer);
}

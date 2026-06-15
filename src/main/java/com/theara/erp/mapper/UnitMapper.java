package com.theara.erp.mapper;

import com.theara.erp.dto.response.UnitResponse;
import com.theara.erp.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    @Mapping(target = "companyId", source = "company.id")
    UnitResponse toResponse(Unit unit);
}

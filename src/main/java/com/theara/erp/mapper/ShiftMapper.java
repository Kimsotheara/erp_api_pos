package com.theara.erp.mapper;

import com.theara.erp.dto.response.ShiftResponse;
import com.theara.erp.entity.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShiftMapper {
    @Mapping(target = "branchId", source = "branch.id")
    ShiftResponse toResponse(Shift shift);
}

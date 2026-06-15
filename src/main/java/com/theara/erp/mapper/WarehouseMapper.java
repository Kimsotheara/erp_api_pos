package com.theara.erp.mapper;

import com.theara.erp.dto.response.WarehouseResponse;
import com.theara.erp.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(target = "branchId", source = "branch.id")
    WarehouseResponse toResponse(Warehouse warehouse);
}

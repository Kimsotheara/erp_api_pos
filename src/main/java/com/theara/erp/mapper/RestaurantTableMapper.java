package com.theara.erp.mapper;

import com.theara.erp.dto.response.RestaurantTableResponse;
import com.theara.erp.entity.RestaurantTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantTableMapper {
    @Mapping(target = "branchId", source = "branch.id")
    RestaurantTableResponse toResponse(RestaurantTable table);
}

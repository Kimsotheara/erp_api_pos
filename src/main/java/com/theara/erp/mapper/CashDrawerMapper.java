package com.theara.erp.mapper;

import com.theara.erp.dto.response.CashDrawerResponse;
import com.theara.erp.dto.response.CashMovementResponse;
import com.theara.erp.entity.CashDrawer;
import com.theara.erp.entity.CashMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CashDrawerMapper {
    @Mapping(target = "branchId", source = "branch.id")
    CashDrawerResponse toResponse(CashDrawer cashDrawer);

    CashMovementResponse toMovementResponse(CashMovement movement);
}

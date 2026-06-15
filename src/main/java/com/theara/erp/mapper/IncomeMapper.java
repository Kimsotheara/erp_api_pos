package com.theara.erp.mapper;

import com.theara.erp.dto.response.IncomeResponse;
import com.theara.erp.entity.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "branchId", source = "branch.id")
    IncomeResponse toResponse(Income income);
}

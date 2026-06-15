package com.theara.erp.mapper;

import com.theara.erp.dto.response.ExpenseResponse;
import com.theara.erp.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "branchId", source = "branch.id")
    ExpenseResponse toResponse(Expense expense);
}

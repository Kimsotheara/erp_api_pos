package com.theara.erp.mapper;

import com.theara.erp.dto.response.BranchResponse;
import com.theara.erp.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "currencyId", source = "currency.id")
    @Mapping(target = "currencyCode", source = "currency.code")
    BranchResponse toResponse(Branch branch);
}

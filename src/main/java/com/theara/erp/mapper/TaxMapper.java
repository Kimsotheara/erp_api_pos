package com.theara.erp.mapper;

import com.theara.erp.dto.response.TaxResponse;
import com.theara.erp.entity.Tax;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxMapper {
    @Mapping(target = "companyId", source = "company.id")
    TaxResponse toResponse(Tax tax);
}

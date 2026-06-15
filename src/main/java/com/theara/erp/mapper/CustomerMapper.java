package com.theara.erp.mapper;

import com.theara.erp.dto.response.CustomerResponse;
import com.theara.erp.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "companyId", source = "company.id")
    CustomerResponse toResponse(Customer customer);
}

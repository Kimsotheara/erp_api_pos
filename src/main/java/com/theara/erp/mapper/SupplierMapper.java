package com.theara.erp.mapper;

import com.theara.erp.dto.response.SupplierContactResponse;
import com.theara.erp.dto.response.SupplierResponse;
import com.theara.erp.entity.Supplier;
import com.theara.erp.entity.SupplierContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    @Mapping(target = "companyId", source = "company.id")
    SupplierResponse toResponse(Supplier supplier);

    SupplierContactResponse toContactResponse(SupplierContact contact);
}

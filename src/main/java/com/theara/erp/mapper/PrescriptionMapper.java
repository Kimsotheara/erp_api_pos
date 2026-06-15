package com.theara.erp.mapper;

import com.theara.erp.dto.response.PrescriptionResponse;
import com.theara.erp.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "customerId", source = "customer.id")
    PrescriptionResponse toResponse(Prescription prescription);
}

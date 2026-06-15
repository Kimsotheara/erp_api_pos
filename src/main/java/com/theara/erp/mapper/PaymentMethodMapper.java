package com.theara.erp.mapper;

import com.theara.erp.dto.response.PaymentMethodResponse;
import com.theara.erp.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    @Mapping(target = "companyId", source = "company.id")
    PaymentMethodResponse toResponse(PaymentMethod paymentMethod);
}

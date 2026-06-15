package com.theara.erp.mapper;

import com.theara.erp.dto.response.CurrencyResponse;
import com.theara.erp.entity.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyResponse toResponse(Currency currency);
}

package com.theara.erp.mapper;

import com.theara.erp.dto.response.ReservationResponse;
import com.theara.erp.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "tableName", source = "table.name")
    @Mapping(target = "customerId", source = "customer.id")
    ReservationResponse toResponse(Reservation reservation);
}

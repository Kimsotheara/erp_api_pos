package com.theara.erp.mapper;

import com.theara.erp.dto.response.KitchenTicketItemResponse;
import com.theara.erp.dto.response.KitchenTicketResponse;
import com.theara.erp.entity.KitchenTicket;
import com.theara.erp.entity.KitchenTicketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KitchenTicketMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "tableId", source = "table.id")
    KitchenTicketResponse toResponse(KitchenTicket ticket);

    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "menuItemName", source = "menuItem.name")
    KitchenTicketItemResponse toItemResponse(KitchenTicketItem item);
}

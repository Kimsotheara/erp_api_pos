package com.theara.erp.mapper;

import com.theara.erp.dto.response.MenuItemResponse;
import com.theara.erp.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "productId", source = "product.id")
    MenuItemResponse toResponse(MenuItem menuItem);
}

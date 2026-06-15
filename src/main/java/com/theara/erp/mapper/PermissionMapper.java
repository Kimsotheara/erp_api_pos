package com.theara.erp.mapper;

import com.theara.erp.dto.response.PermissionResponse;
import com.theara.erp.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toResponse(Permission permission);
}

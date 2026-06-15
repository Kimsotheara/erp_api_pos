package com.theara.erp.mapper;

import com.theara.erp.dto.response.RoleResponse;
import com.theara.erp.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {
    @Mapping(target = "companyId", source = "company.id")
    RoleResponse toResponse(Role role);
}

package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RoleRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.RoleResponse;
import com.theara.erp.entity.Role;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse getRoleById(Long id);
    RoleResponse updateRole(Long id, RoleRequest request);
    PageAbleResponse<Role, RoleResponse, Void> getRoles(PageAbleRequest<Void> request);
    void deleteRole(Long id);
}

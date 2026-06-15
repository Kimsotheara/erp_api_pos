package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PermissionRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PermissionResponse;
import com.theara.erp.entity.Permission;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    PermissionResponse getPermissionById(Long id);
    PermissionResponse updatePermission(Long id, PermissionRequest request);
    PageAbleResponse<Permission, PermissionResponse, Void> getPermissions(PageAbleRequest<Void> request);
    void deletePermission(Long id);
}

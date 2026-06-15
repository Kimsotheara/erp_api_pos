package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PermissionRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PermissionResponse;
import com.theara.erp.entity.Permission;
import com.theara.erp.mapper.PermissionMapper;
import com.theara.erp.repository.PermissionRepository;
import com.theara.erp.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        if (permissionRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Permission code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        Permission permission = Permission.builder()
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .build();
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    @Override @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long id) {
        return permissionMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        Permission permission = findById(id);
        if (permissionRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Permission code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        permission.setCode(request.getCode().toUpperCase());
        permission.setDescription(request.getDescription());
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Permission, PermissionResponse, Void> getPermissions(PageAbleRequest<Void> request) {
        Page<Permission> page = permissionRepository.findAll(request.getPageAble());
        List<PermissionResponse> list = page.getContent().stream().map(permissionMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deletePermission(Long id) {
        permissionRepository.delete(findById(id));
    }

    private Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Permission " + ErrorCode.NOT_FOUND.getDescription()));
    }
}

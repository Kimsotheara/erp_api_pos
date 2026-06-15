package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RoleRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.RoleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Permission;
import com.theara.erp.entity.Role;
import com.theara.erp.mapper.RoleMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.PermissionRepository;
import com.theara.erp.repository.RoleRepository;
import com.theara.erp.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByCompanyIdAndNameIgnoreCase(request.getCompanyId(), request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Role '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        Role role = new Role();
        apply(role, request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        return roleMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = findById(id);
        if (roleRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(request.getCompanyId(), request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Role '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        apply(role, request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Role, RoleResponse, Void> getRoles(PageAbleRequest<Void> request) {
        Page<Role> page = roleRepository.findAll(request.getPageAble());
        List<RoleResponse> list = page.getContent().stream().map(roleMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteRole(Long id) {
        Role role = findById(id);
        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "System roles cannot be deleted");
        }
        role.setIsDeleted(1);
        roleRepository.save(role);
    }

    private void apply(Role role, RoleRequest r) {
        Company company = companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company"));
        role.setCompany(company);
        role.setName(r.getName());
        role.setDescription(r.getDescription());
        if (r.getIsSystem() != null) role.setIsSystem(r.getIsSystem());
        role.setPermissions(resolvePermissions(r.getPermissionIds()));
    }

    private java.util.Set<Permission> resolvePermissions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        List<Permission> found = permissionRepository.findByIdIn(ids);
        if (found.size() != new HashSet<>(ids).size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more permissionIds are invalid");
        }
        return new HashSet<>(found);
    }

    private Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> notFound("Role"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}

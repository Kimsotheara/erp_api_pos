package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UserRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.UserResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Role;
import com.theara.erp.entity.User;
import com.theara.erp.mapper.UserMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.RoleRepository;
import com.theara.erp.repository.UserRepository;
import com.theara.erp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j @Service @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByCompanyIdAndUsernameIgnoreCase(request.getCompanyId(), request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username '" + request.getUsername() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required when creating a user");
        }
        User user = new User();
        apply(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findById(id);
        if (userRepository.existsByCompanyIdAndUsernameIgnoreCaseAndIdNot(request.getCompanyId(), request.getUsername(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username '" + request.getUsername() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        apply(user, request);
        // Only rotate the password when a new one is supplied.
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<User, UserResponse, Void> getUsers(PageAbleRequest<Void> request) {
        Page<User> page = userRepository.findAll(request.getPageAble());
        List<UserResponse> list = page.getContent().stream().map(userMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        user.setIsDeleted(1);
        userRepository.save(user);
    }

    private void apply(User user, UserRequest r) {
        Company company = companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company"));
        user.setCompany(company);
        user.setUsername(r.getUsername());
        user.setEmail(r.getEmail());
        user.setFullName(r.getFullName());
        user.setPhone(r.getPhone());
        user.setImage(r.getImage());
        user.setDefaultBranch(r.getDefaultBranchId() == null ? null
                : branchRepository.findById(r.getDefaultBranchId()).orElseThrow(() -> notFound("Branch")));
        if (r.getIsActive() != null) user.setIsActive(r.getIsActive());
        user.setRoles(resolveRoles(r.getRoleIds()));
        user.setBranches(resolveBranches(r.getBranchIds()));
    }

    private Set<Role> resolveRoles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        List<Role> found = roleRepository.findByIdIn(ids);
        if (found.size() != new HashSet<>(ids).size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more roleIds are invalid");
        }
        return new HashSet<>(found);
    }

    private Set<Branch> resolveBranches(List<Long> ids) {
        Set<Branch> branches = new HashSet<>();
        if (ids == null) return branches;
        for (Long id : ids) {
            branches.add(branchRepository.findById(id).orElseThrow(() -> notFound("Branch " + id)));
        }
        return branches;
    }

    private User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> notFound("User"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}

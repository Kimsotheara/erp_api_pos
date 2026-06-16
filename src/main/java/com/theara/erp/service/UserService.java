package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UserRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.UserResponse;
import com.theara.erp.entity.User;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest request);
    PageAbleResponse<User, UserResponse, Void> getUsers(PageAbleRequest<Void> request);
    UserResponse setActiveStatus(Long id, Boolean isActive);
    void deleteUser(Long id);
}

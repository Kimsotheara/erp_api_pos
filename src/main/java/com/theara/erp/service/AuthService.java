package com.theara.erp.service;

import com.theara.erp.dto.request.LoginRequest;
import com.theara.erp.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}

package com.qoder.mall.service;

import com.qoder.mall.dto.request.LoginRequest;
import com.qoder.mall.dto.request.RegisterRequest;
import com.qoder.mall.dto.response.LoginResponse;
import com.qoder.mall.dto.response.UserInfoResponse;

public interface IAuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserInfoResponse getUserInfo(Long userId);
}

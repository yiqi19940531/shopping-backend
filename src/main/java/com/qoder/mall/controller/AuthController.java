package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.LoginRequest;
import com.qoder.mall.dto.request.RegisterRequest;
import com.qoder.mall.dto.response.LoginResponse;
import com.qoder.mall.dto.response.UserInfoResponse;
import com.qoder.mall.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录注册相关接口")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<UserInfoResponse> getUserInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(authService.getUserInfo(userId));
    }
}

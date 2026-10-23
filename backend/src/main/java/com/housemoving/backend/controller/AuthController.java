package com.housemoving.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.housemoving.backend.dto.auth.LoginRequest;
import com.housemoving.backend.dto.auth.LoginResponse;
import com.housemoving.backend.dto.auth.RefreshTokenRequest;
import com.housemoving.backend.dto.auth.RegisterRequest;
import com.housemoving.backend.dto.auth.RegisterResponse;
import com.housemoving.backend.dto.auth.TokenRefreshResponse;
import com.housemoving.backend.dto.common.ApiResponse;
import com.housemoving.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. API Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ApiResponse.created(response);
    }

    // 2. API Đăng nhập (Trả về Access Token & Refresh Token)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response, "Đăng nhập thành công");
    }

    // 3. API Cấp lại Access Token từ Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success(response, "Làm mới token thành công");
    }

    // 4. API Đăng xuất (Thu hồi Refresh Token)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success("Đăng xuất thành công", "Đã thu hồi token và đăng xuất thành công");
    }
}

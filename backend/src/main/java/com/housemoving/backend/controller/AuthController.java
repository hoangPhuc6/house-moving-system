package com.housemoving.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.housemoving.backend.dto.auth.LoginRequest;
import com.housemoving.backend.dto.auth.LoginResponse;
import com.housemoving.backend.dto.auth.RegisterRequest;
import com.housemoving.backend.dto.auth.RegisterResponse;
import com.housemoving.backend.dto.common.ApiResponse;
import com.housemoving.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService sensibleAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = sensibleAuthService.register(request);
        return ApiResponse.created(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = sensibleAuthService.login(request);
        return ApiResponse.success(response, "Đăng nhập thành công");
    }

}

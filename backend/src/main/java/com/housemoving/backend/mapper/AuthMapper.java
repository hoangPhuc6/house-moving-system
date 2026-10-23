package com.housemoving.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.housemoving.backend.dto.auth.LoginResponse;
import com.housemoving.backend.dto.auth.RegisterRequest;
import com.housemoving.backend.dto.auth.RegisterResponse;
import com.housemoving.backend.entity.User;
import com.housemoving.backend.security.CustomUserDetails;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    // 1. Chuyển từ RegisterRequest sang User entity (bao gồm fullName, email, phoneNumber)
    @Mapping(target = "password", ignore = true) // Mật khẩu sẽ được mã hóa riêng bằng PasswordEncoder
    @Mapping(target = "isActive", constant = "true")
    User toUser(RegisterRequest request);

    // 2. Chuyển từ User sang RegisterResponse (tất cả thông tin id, email, fullName, phoneNumber đều từ User)
    RegisterResponse toRegisterResponse(User user);

    // 3. Ghép thông tin từ CustomUserDetails, Token và Roles sang LoginResponse
    @Mapping(target = "userId", source = "userDetails.id")
    @Mapping(target = "email", source = "userDetails.email")
    @Mapping(target = "fullName", source = "userDetails.fullName")
    @Mapping(target = "tokenType", constant = "Bearer")
    LoginResponse toLoginResponse(CustomUserDetails userDetails, String accessToken, List<String> roles);
}

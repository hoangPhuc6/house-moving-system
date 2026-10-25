package com.housemoving.backend.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.housemoving.backend.dto.auth.LoginResponse;
import com.housemoving.backend.dto.auth.RegisterRequest;
import com.housemoving.backend.dto.auth.RegisterResponse;
import com.housemoving.backend.dto.auth.UserResponse;
import com.housemoving.backend.entity.Role;
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

    // 3. Ghép thông tin từ CustomUserDetails, Token, RefreshToken và Roles sang LoginResponse
    @Mapping(target = "userId", source = "userDetails.id")
    @Mapping(target = "email", source = "userDetails.email")
    @Mapping(target = "fullName", source = "userDetails.fullName")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "refreshToken", source = "refreshToken")
    LoginResponse toLoginResponse(CustomUserDetails userDetails, String accessToken, String refreshToken, List<String> roles);

    // 4. Chuyển từ User sang UserResponse (dùng cho API /me)
    @Mapping(target = "customerCode", source = "customer.customerCode")
    @Mapping(target = "birthday", source = "customer.birthday")
    @Mapping(target = "staffCode", source = "staff.staffCode")
    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(role -> role.getCode() != null && role.getCode().startsWith("ROLE_") 
                        ? role.getCode() 
                        : "ROLE_" + role.getCode())
                .toList();
    }
}

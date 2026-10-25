package com.housemoving.backend.dto.auth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private List<String> roles;
    private Boolean isActive;

    // Thông tin khách hàng (nếu là khách hàng)
    private String customerCode;
    private LocalDate birthday;

    // Thông tin nhân viên (nếu là nhân sự/điều phối/tài xế)
    private String staffCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

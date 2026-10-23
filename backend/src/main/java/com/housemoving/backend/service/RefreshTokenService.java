package com.housemoving.backend.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housemoving.backend.entity.RefreshToken;
import com.housemoving.backend.entity.User;
import com.housemoving.backend.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    // 1. Tạo Opaque Refresh Token mới cho người dùng
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // 2. Tìm token theo chuỗi Opaque String
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token không tồn tại!"));
    }

    // 3. Kiểm tra tính hợp lệ và thời hạn của Token
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Phát hiện Replay Attack: Nếu token đã bị thu hồi mà vẫn cố tình gửi lên refresh
        if (Boolean.TRUE.equals(token.getRevoked())) {
            log.warn("Cảnh báo bảo mật: Phát hiện sử dụng Refresh Token đã bị thu hồi của User ID: {}. Đang thu hồi toàn bộ token của tài khoản này!",
                    token.getUser().getId());
            refreshTokenRepository.revokeAllByUser(token.getUser());
            throw new IllegalArgumentException("Phiên đăng nhập không hợp lệ hoặc nghi vấn bị đánh cắp! Vui lòng đăng nhập lại.");
        }

        // Kiểm tra hết hạn
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new IllegalArgumentException("Refresh token đã hết hạn! Vui lòng đăng nhập lại.");
        }

        return token;
    }

    // 4. Cơ chế Rotation: Thu hồi token cũ và cấp token mới
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // Đánh dấu thu hồi token cũ
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Sinh token mới cho cùng user
        return createRefreshToken(oldToken.getUser());
    }

    // 5. Thu hồi token khi người dùng đăng xuất (Logout)
    @Transactional
    public void revokeToken(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
}

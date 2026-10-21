package com.housemoving.backend.exception;

import com.housemoving.backend.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi không tìm thấy tài nguyên (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND");
    }

    // 2. Bắt lỗi Validate dữ liệu đầu vào @Valid (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", errors, "VALIDATION_FAILED");
    }

    // 3. Bắt lỗi sai thông tin đăng nhập (401 Unauthorized)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác", "UNAUTHORIZED");
    }

    // 4. Bắt lỗi không có quyền truy cập API (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này", "FORBIDDEN");
    }

    // 5. Bắt các lỗi nghiệp vụ logic khác (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalState(IllegalStateException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST");
    }

    // 6. Bắt tất cả các lỗi ngoại lệ chưa lường trước (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // In ra console để debug
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi hệ thống: " + ex.getMessage(),
                "INTERNAL_SERVER_ERROR");
    }
}


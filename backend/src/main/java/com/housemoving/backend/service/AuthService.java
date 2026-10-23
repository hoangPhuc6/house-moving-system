package com.housemoving.backend.service;

import com.housemoving.backend.repository.CustomerRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housemoving.backend.dto.auth.LoginRequest;
import com.housemoving.backend.dto.auth.LoginResponse;
import com.housemoving.backend.dto.auth.RegisterRequest;
import com.housemoving.backend.dto.auth.RegisterResponse;
import com.housemoving.backend.dto.auth.TokenRefreshResponse;
import com.housemoving.backend.entity.Customer;
import com.housemoving.backend.entity.RefreshToken;
import com.housemoving.backend.entity.Role;
import com.housemoving.backend.entity.User;
import com.housemoving.backend.mapper.AuthMapper;
import com.housemoving.backend.repository.RoleRepository;
import com.housemoving.backend.repository.UserRepository;
import com.housemoving.backend.security.CustomUserDetails;
import com.housemoving.backend.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email " + req.getEmail() + " đã được sử dụng!");
        }

        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại " + req.getPhoneNumber() + " đã được sử dụng!");
        }

        Role customerRole = roleRepository.findByCode("ROLE_CUSTOMER")
                .or(() -> roleRepository.findByCode("CUSTOMER"))
                .orElseThrow(() -> new RuntimeException("Chưa có role CUSTOMER trong hệ thống!"));

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .roles(Set.of(customerRole))
                .build();

        userRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .customerCode("CUS-" + System.currentTimeMillis())
                .build();

        customerRepository.save(customer);

        return authMapper.toRegisterResponse(user);

    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateToken(userDetails);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng!"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return authMapper.toLoginResponse(userDetails, accessToken, refreshToken.getToken(), roles);
    }

    @Transactional
    public TokenRefreshResponse refreshToken(String reqRefreshToken) {
        RefreshToken token = refreshTokenService.findByToken(reqRefreshToken);
        token = refreshTokenService.verifyExpiration(token);

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(token);

        User user = token.getUser();

        CustomUserDetails userDetail = CustomUserDetails.fromUser(user);
        String newAccessToken = jwtTokenProvider.generateToken(userDetail);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
        SecurityContextHolder.clearContext();
    }
}

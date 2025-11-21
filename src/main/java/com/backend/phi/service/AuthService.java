package com.backend.phi.service;

import com.backend.phi.dto.auth.ForgotPasswordRequest;
import com.backend.phi.dto.auth.LoginRequest;
import com.backend.phi.dto.auth.LoginResponse;
import com.backend.phi.dto.auth.ResetPasswordRequest;
import com.backend.phi.dto.auth.SignupRequest;
import com.backend.phi.dto.user.UserResponse;
import com.backend.phi.entity.PasswordResetToken;
import com.backend.phi.entity.User;
import com.backend.phi.repository.PasswordResetTokenRepository;
import com.backend.phi.repository.UserRepository;
import com.backend.phi.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다");
        }

        // 닉네임 중복 확인 (필요시)
        // if (userRepository.existsByNickname(request.getNickname())) {
        //     throw new RuntimeException("이미 사용 중인 닉네임입니다");
        // }

        // 새 사용자 생성
        User user = new User();
        user.setNickname(request.getNickname());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 해싱
        user.setDob(request.getDob());
        
        if (request.getGender() != null) {
            try {
                user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setGender(User.Gender.OTHER);
            }
        }
        
        user.setLastLocation(request.getLastLocation());
        user.setCredScore(0);
        user.setUserScore(0);

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId());
        UserResponse userResponse = toUserResponse(user);

        return new LoginResponse(token, userResponse);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 사용자를 찾을 수 없습니다"));

        // 기존 미사용 토큰 무효화
        tokenRepository.invalidateUserTokens(user);

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(Instant.now().plusSeconds(3600)); // 1시간 후 만료
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // TODO: 실제 이메일 전송 구현
        // 현재는 개발 환경이므로 콘솔에 토큰 출력
        System.out.println("=== 비밀번호 재설정 토큰 생성 ===");
        System.out.println("이메일: " + user.getEmail());
        System.out.println("토큰: " + token);
        System.out.println("만료 시간: " + resetToken.getExpiresAt());
        System.out.println("비밀번호 재설정 링크: http://your-app.com/reset-password?token=" + token);
        // 실제 서비스에서는 이메일로 링크를 전송해야 합니다
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않거나 만료된 토큰입니다"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("토큰이 만료되었습니다");
        }

        if (resetToken.getUsed()) {
            throw new RuntimeException("이미 사용된 토큰입니다");
        }

        // 비밀번호 업데이트
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 토큰 사용 처리
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // 해당 사용자의 다른 모든 토큰 무효화
        tokenRepository.invalidateUserTokens(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getNickname(),
                user.getName(),
                user.getEmail(),
                user.getCredScore(),
                user.getUserScore(),
                user.getDob(),
                user.getGender() != null ? user.getGender().name() : null,
                user.getLastLocation(),
                user.getUserCreatedAt()
        );
    }
}


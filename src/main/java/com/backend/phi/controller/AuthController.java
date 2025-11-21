package com.backend.phi.controller;

import com.backend.phi.dto.auth.ForgotPasswordRequest;
import com.backend.phi.dto.auth.LoginRequest;
import com.backend.phi.dto.auth.LoginResponse;
import com.backend.phi.dto.auth.ResetPasswordRequest;
import com.backend.phi.dto.auth.SignupRequest;
import com.backend.phi.dto.user.UserResponse;
import com.backend.phi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        System.out.println("=== 회원가입 요청 수신 ===");
        System.out.println("이메일: " + request.getEmail());
        System.out.println("닉네임: " + request.getNickname());
        System.out.println("이름: " + request.getName());
        
        try {
            UserResponse user = authService.signup(request);
            System.out.println("회원가입 성공 - User ID: " + user.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            System.out.println("회원가입 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("=== 로그인 요청 수신 ===");
        System.out.println("이메일: " + request.getEmail());
        
        try {
            LoginResponse response = authService.login(request);
            System.out.println("로그인 성공 - User ID: " + response.getUser().getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("로그인 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        System.out.println("=== 비밀번호 찾기 요청 수신 ===");
        System.out.println("이메일: " + request.getEmail());
        
        try {
            authService.forgotPassword(request);
            System.out.println("비밀번호 재설정 토큰 생성 완료");
            return ResponseEntity.ok(new MessageResponse("비밀번호 재설정 링크가 이메일로 전송되었습니다"));
        } catch (RuntimeException e) {
            System.out.println("비밀번호 찾기 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        System.out.println("=== 비밀번호 재설정 요청 수신 ===");
        
        try {
            authService.resetPassword(request);
            System.out.println("비밀번호 재설정 성공");
            return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 변경되었습니다"));
        } catch (RuntimeException e) {
            System.out.println("비밀번호 재설정 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Error response DTO
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Success message response DTO
    private static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}


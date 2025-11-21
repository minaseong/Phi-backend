package com.backend.phi.controller;

import com.backend.phi.dto.user.UserRequest;
import com.backend.phi.dto.user.UserResponse;
import com.backend.phi.entity.User;
import com.backend.phi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------- Helpers: mapping between Entity and DTOs ----------

    private UserResponse toResponse(User user) {
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

    private void updateEntityFromRequest(User user, UserRequest request) {
        user.setNickname(request.getNickname());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());

        if (request.getGender() != null) {
            user.setGender(User.Gender.valueOf(request.getGender()));
        }

        user.setLastLocation(request.getLastLocation());

        if (request.getCredScore() != null) {
            user.setCredScore(request.getCredScore());
        }
        if (request.getUserScore() != null) {
            user.setUserScore(request.getUserScore());
        }

        // For now: store raw password (later you will hash in service)
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
    }

    // ---------- CRUD endpoints ----------

    // GET /api/users/v1
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/users/v1/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(toResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users/v1
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        User user = new User();
        updateEntityFromRequest(user, request);
        User saved = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // PUT /api/users/v1/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request
    ) {
        return userService.getUserById(id)
                .map(existing -> {
                    updateEntityFromRequest(existing, request);
                    User updated = userService.saveUser(existing);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/v1/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // 기존 로그인 엔드포인트는 /api/auth/login으로 이동했습니다
    // JWT 토큰 기반 인증을 사용하세요
}
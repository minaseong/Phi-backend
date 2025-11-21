# 비밀번호 찾기/재설정 API 가이드

## 📍 API 엔드포인트

### 1. 비밀번호 찾기 (Forgot Password)

**엔드포인트:**
```
POST /api/auth/forgot-password
```

**요청 본문:**
```json
{
  "email": "user@example.com"
}
```

**성공 응답 (200 OK):**
```json
{
  "message": "비밀번호 재설정 링크가 이메일로 전송되었습니다"
}
```

**에러 응답 (400 Bad Request):**
```json
{
  "message": "해당 이메일로 등록된 사용자를 찾을 수 없습니다"
}
```

**동작 방식:**
1. 이메일로 사용자 조회
2. 기존 미사용 토큰 무효화
3. 새로운 비밀번호 재설정 토큰 생성 (UUID, 1시간 유효)
4. 토큰을 DB에 저장
5. **개발 환경**: 콘솔에 토큰 출력
6. **프로덕션**: 이메일로 재설정 링크 전송 (구현 필요)

---

### 2. 비밀번호 재설정 (Reset Password)

**엔드포인트:**
```
POST /api/auth/reset-password
```

**요청 본문:**
```json
{
  "token": "uuid-token-string",
  "newPassword": "newpassword123"
}
```

**성공 응답 (200 OK):**
```json
{
  "message": "비밀번호가 성공적으로 변경되었습니다"
}
```

**에러 응답 (400 Bad Request):**
```json
{
  "message": "유효하지 않거나 만료된 토큰입니다"
}
```

또는

```json
{
  "message": "토큰이 만료되었습니다"
}
```

**동작 방식:**
1. 토큰으로 재설정 토큰 조회
2. 토큰 유효성 검증 (만료 여부, 사용 여부)
3. 새 비밀번호 해싱 후 저장
4. 토큰 사용 처리
5. 해당 사용자의 다른 모든 토큰 무효화

---

## 🗄️ 데이터베이스

### PasswordResetToken 테이블

자동으로 생성되는 테이블 구조:

- `token_id`: Primary Key
- `token`: UUID 토큰 문자열 (Unique)
- `user_id`: User 테이블 Foreign Key
- `expires_at`: 토큰 만료 시간 (1시간 후)
- `used`: 사용 여부 (기본값: false)
- `created_at`: 생성 시간

---

## 🔧 개발 환경에서 테스트

### 1. 비밀번호 찾기 요청
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

**서버 콘솔 출력:**
```
=== 비밀번호 찾기 요청 수신 ===
이메일: user@example.com
=== 비밀번호 재설정 토큰 생성 ===
이메일: user@example.com
토큰: 550e8400-e29b-41d4-a716-446655440000
만료 시간: 2024-01-01T13:00:00Z
비밀번호 재설정 링크: http://your-app.com/reset-password?token=550e8400-e29b-41d4-a716-446655440000
비밀번호 재설정 토큰 생성 완료
```

### 2. 비밀번호 재설정 요청
```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "newpassword123"
  }'
```

---

## 📱 프론트엔드 연동

### Swift 모델

```swift
// ForgotPasswordRequest.swift
struct ForgotPasswordRequest: Codable {
    let email: String
}

// ResetPasswordRequest.swift
struct ResetPasswordRequest: Codable {
    let token: String
    let newPassword: String
}

// MessageResponse.swift
struct MessageResponse: Codable {
    let message: String
}
```

### API 호출 예시

```swift
class AuthAPI {
    // 비밀번호 찾기
    static func forgotPassword(email: String) async throws -> MessageResponse {
        let request = ForgotPasswordRequest(email: email)
        // API 호출 로직
    }
    
    // 비밀번호 재설정
    static func resetPassword(token: String, newPassword: String) async throws -> MessageResponse {
        let request = ResetPasswordRequest(token: token, newPassword: newPassword)
        // API 호출 로직
    }
}
```

---

## ⚠️ 중요 사항

### 1. 보안
- 토큰은 UUID로 생성되어 예측 불가능
- 토큰 만료 시간: 1시간
- 한 번 사용된 토큰은 재사용 불가
- 비밀번호는 BCrypt로 해싱되어 저장

### 2. 이메일 전송 (프로덕션)
현재는 개발 환경이므로 콘솔에 토큰을 출력합니다. 프로덕션 환경에서는:

1. 이메일 서비스 설정 (예: SendGrid, AWS SES, SMTP)
2. `AuthService.forgotPassword()` 메서드에서 이메일 전송 로직 추가
3. 이메일 템플릿에 재설정 링크 포함

예시:
```java
// 이메일 서비스 추가 후
emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
```

### 3. 토큰 정리
만료된 토큰은 주기적으로 정리하는 스케줄러를 추가할 수 있습니다:

```java
@Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
public void cleanupExpiredTokens() {
    tokenRepository.deleteExpiredTokens(Instant.now());
}
```

---

## 🔄 사용자 플로우

1. **사용자가 비밀번호를 잊음**
   - 로그인 화면에서 "Forgot Password?" 클릭
   - 이메일 입력
   - `POST /api/auth/forgot-password` 호출

2. **이메일로 재설정 링크 수신** (프로덕션)
   - 이메일에서 링크 클릭
   - 앱의 비밀번호 재설정 화면으로 이동 (토큰 포함)

3. **새 비밀번호 설정**
   - 새 비밀번호 입력
   - `POST /api/auth/reset-password` 호출
   - 성공 시 로그인 화면으로 이동

---

## 🧪 테스트 시나리오

### 정상 플로우
1. ✅ 존재하는 이메일로 비밀번호 찾기 요청
2. ✅ 서버 콘솔에서 토큰 확인
3. ✅ 토큰으로 비밀번호 재설정
4. ✅ 새 비밀번호로 로그인 성공

### 에러 케이스
1. ❌ 존재하지 않는 이메일 → "해당 이메일로 등록된 사용자를 찾을 수 없습니다"
2. ❌ 만료된 토큰 → "토큰이 만료되었습니다"
3. ❌ 이미 사용된 토큰 → "이미 사용된 토큰입니다"
4. ❌ 유효하지 않은 토큰 → "유효하지 않거나 만료된 토큰입니다"

---

## 📝 참고

- 토큰은 1시간 동안 유효합니다
- 한 사용자당 동시에 여러 개의 활성 토큰을 가질 수 있지만, 비밀번호 재설정 시 모든 토큰이 무효화됩니다
- 이메일 전송 기능은 별도로 구현해야 합니다


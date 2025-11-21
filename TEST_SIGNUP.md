# 회원가입 API 테스트 가이드

## ✅ 백엔드 설정 확인 완료

백엔드는 회원가입 요청을 정상적으로 받을 수 있도록 설정되어 있습니다:

1. ✅ **엔드포인트**: `POST /api/auth/signup`
2. ✅ **Security 설정**: `/api/auth/**` 경로는 인증 없이 접근 가능 (`permitAll()`)
3. ✅ **CORS 설정**: 모든 origin 허용
4. ✅ **로깅 추가**: 요청 수신 시 콘솔에 로그 출력

## 🧪 테스트 방법

### 1. curl로 직접 테스트

터미널에서 다음 명령어를 실행하세요:

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "testuser",
    "name": "테스트",
    "email": "test@example.com",
    "password": "password123",
    "dob": "1990-01-01",
    "gender": "M",
    "lastLocation": "서울"
  }'
```

**성공 응답 예시:**
```json
{
  "userId": 1,
  "nickname": "testuser",
  "name": "테스트",
  "email": "test@example.com",
  "credScore": 0,
  "userScore": 0,
  "dob": "1990-01-01",
  "gender": "M",
  "lastLocation": "서울",
  "userCreatedAt": "2024-01-01T00:00:00Z"
}
```

### 2. 서버 로그 확인

서버 콘솔에서 다음과 같은 로그가 출력되면 요청이 정상적으로 수신된 것입니다:

```
=== 회원가입 요청 수신 ===
이메일: test@example.com
닉네임: testuser
이름: 테스트
회원가입 성공 - User ID: 1
```

### 3. 프론트엔드에서 테스트

프론트엔드에서 회원가입 버튼을 클릭하면:

1. **서버 콘솔에 로그가 출력되는지 확인**
   - 로그가 보이면 → 요청이 정상적으로 도착한 것
   - 로그가 안 보이면 → 네트워크 문제 또는 서버 미실행

2. **에러 메시지 확인**
   - 프론트엔드에서 받은 에러 메시지를 확인
   - 서버 로그의 에러 메시지와 비교

## 🔍 문제 해결

### 요청이 도착하지 않는 경우

1. **서버가 실행 중인지 확인**
   ```bash
   # 서버 실행
   ./mvnw spring-boot:run
   ```

2. **포트 확인**
   - 기본 포트: 8080
   - `application.properties`에서 `server.port` 확인

3. **네트워크 확인**
   - iOS 시뮬레이터: `http://localhost:8080`
   - 실제 기기: `http://[Mac의 IP]:8080`
   - Mac IP 확인: `ifconfig | grep "inet " | grep -v 127.0.0.1`

4. **CORS 에러 확인**
   - 브라우저 개발자 도구의 Network 탭에서 CORS 에러 확인
   - 백엔드 `CorsConfig.java` 확인

### 요청은 도착하지만 에러가 발생하는 경우

1. **이메일 중복 에러**
   ```
   "이미 사용 중인 이메일입니다"
   ```
   → 다른 이메일로 시도

2. **유효성 검증 에러**
   - `nickname`: 2-20자
   - `name`: 1-30자
   - `email`: 유효한 이메일 형식
   - `password`: 6자 이상

3. **DB 연결 에러**
   - MySQL 서버가 실행 중인지 확인
   - `application.properties`의 DB 설정 확인

## 📊 요청 흐름

```
프론트엔드 (iOS)
    ↓
POST /api/auth/signup
    ↓
CorsFilter (CORS 처리)
    ↓
SecurityFilterChain (인증 체크 - permitAll이므로 통과)
    ↓
AuthController.signup() ← 여기서 로그 출력
    ↓
AuthService.signup()
    ↓
UserRepository.save() → DB 저장
    ↓
응답 반환
```

## 💡 디버깅 팁

1. **서버 로그 확인**: 가장 확실한 방법
2. **프론트엔드 네트워크 로그**: Xcode의 Network Inspector 사용
3. **DB 직접 확인**: MySQL에서 User 테이블 조회
   ```sql
   SELECT * FROM User;
   ```


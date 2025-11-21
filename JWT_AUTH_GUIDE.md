# JWT 인증 시스템 가이드 (프론트엔드용)

## 🔐 인증 시스템 개요

이제 백엔드는 JWT(JSON Web Token) 기반 인증을 사용합니다. 모든 사용자 데이터는 DB에 저장되며, 비밀번호는 안전하게 암호화되어 저장됩니다.

## 📍 인증 API 엔드포인트

### 1. 회원가입 (Signup)

**엔드포인트:**
```
POST /api/auth/signup
```

**요청 본문:**
```json
{
  "nickname": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "password": "password123",
  "dob": "1990-01-01",
  "gender": "M",
  "lastLocation": "서울"
}
```

**성공 응답 (201 Created):**
```json
{
  "userId": 1,
  "nickname": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "credScore": 0,
  "userScore": 0,
  "dob": "1990-01-01",
  "gender": "M",
  "lastLocation": "서울",
  "userCreatedAt": "2024-01-01T00:00:00Z"
}
```

**에러 응답 (400 Bad Request):**
```json
{
  "message": "이미 사용 중인 이메일입니다"
}
```

**유효성 검증 규칙:**
- `nickname`: 필수, 2-20자
- `name`: 필수, 1-30자
- `email`: 필수, 유효한 이메일 형식
- `password`: 필수, 6자 이상
- `gender`: 선택, "M", "F", "OTHER"

---

### 2. 로그인 (Login)

**엔드포인트:**
```
POST /api/auth/login
```

**요청 본문:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**성공 응답 (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE3MDQwNjcyMDAsImV4cCI6MTcwNDE1MzYwMH0...",
  "tokenType": "Bearer",
  "user": {
    "userId": 1,
    "nickname": "user123",
    "name": "홍길동",
    "email": "user@example.com",
    "credScore": 0,
    "userScore": 0,
    "dob": "1990-01-01",
    "gender": "M",
    "lastLocation": "서울",
    "userCreatedAt": "2024-01-01T00:00:00Z"
  }
}
```

**에러 응답 (401 Unauthorized):**
```json
{
  "message": "이메일 또는 비밀번호가 올바르지 않습니다"
}
```

---

### 3. 비밀번호 찾기 (Forgot Password)

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

**참고**: 개발 환경에서는 서버 콘솔에 재설정 토큰이 출력됩니다. 프로덕션에서는 이메일로 전송됩니다.

---

### 4. 비밀번호 재설정 (Reset Password)

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

**참고**: 토큰은 1시간 동안 유효하며, 한 번 사용되면 재사용할 수 없습니다.

---

## 🔑 JWT 토큰 사용 방법

### 토큰 저장
로그인 성공 후 받은 `token` 값을 안전하게 저장하세요 (예: Keychain, UserDefaults).

### 인증이 필요한 API 호출
인증이 필요한 모든 API 요청에 다음 헤더를 추가하세요:

```
Authorization: Bearer {token}
```

예시:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 토큰 만료 시간
- 기본 만료 시간: **24시간**
- 토큰이 만료되면 다시 로그인해야 합니다

---

## 📱 Swift 코드 예시

### 1. 모델 정의

```swift
// SignupRequest.swift
struct SignupRequest: Codable {
    let nickname: String
    let name: String
    let email: String
    let password: String
    let dob: String?
    let gender: String?
    let lastLocation: String?
}

// LoginRequest.swift
struct LoginRequest: Codable {
    let email: String
    let password: String
}

// LoginResponse.swift
struct LoginResponse: Codable {
    let token: String
    let tokenType: String
    let user: UserResponse
}

// UserResponse.swift
struct UserResponse: Codable {
    let userId: Int64?
    let nickname: String?
    let name: String?
    let email: String?
    let credScore: Int?
    let userScore: Int?
    let dob: String?
    let gender: String?
    let lastLocation: String?
    let userCreatedAt: String?
}

// ErrorResponse.swift
struct ErrorResponse: Codable {
    let message: String
}
```

### 2. 토큰 관리 클래스

```swift
import Foundation

class TokenManager {
    static let shared = TokenManager()
    private let tokenKey = "jwt_token"
    
    private init() {}
    
    // 토큰 저장
    func saveToken(_ token: String) {
        UserDefaults.standard.set(token, forKey: tokenKey)
    }
    
    // 토큰 조회
    func getToken() -> String? {
        return UserDefaults.standard.string(forKey: tokenKey)
    }
    
    // 토큰 삭제 (로그아웃 시)
    func deleteToken() {
        UserDefaults.standard.removeObject(forKey: tokenKey)
    }
    
    // 토큰 존재 여부 확인
    func hasToken() -> Bool {
        return getToken() != nil
    }
}
```

### 3. 업데이트된 API 클라이언트

```swift
import Foundation

class APIClient {
    private let baseURL = "http://localhost:8080" // 또는 실제 서버 IP
    static let shared = APIClient()
    
    private init() {}
    
    // Generic HTTP Request 함수 (인증 토큰 지원)
    func request<T: Decodable>(
        endpoint: String,
        method: String = "GET",
        body: Encodable? = nil,
        responseType: T.Type,
        requiresAuth: Bool = false
    ) async throws -> T {
        guard let url = URL(string: "\(baseURL)\(endpoint)") else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // 인증 토큰 추가
        if requiresAuth {
            if let token = TokenManager.shared.getToken() {
                request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            } else {
                throw APIError.unauthorized
            }
        }
        
        if let body = body {
            request.httpBody = try JSONEncoder().encode(body)
        }
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard (200...299).contains(httpResponse.statusCode) else {
            // 에러 응답 파싱 시도
            if let errorResponse = try? JSONDecoder().decode(ErrorResponse.self, from: data) {
                throw APIError.serverError(errorResponse.message)
            }
            throw APIError.httpError(httpResponse.statusCode)
        }
        
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return try decoder.decode(T.self, from: data)
    }
}

enum APIError: Error {
    case invalidURL
    case invalidResponse
    case httpError(Int)
    case unauthorized
    case serverError(String)
}
```

### 4. 인증 서비스

```swift
class AuthService {
    private let apiClient = APIClient.shared
    
    // 회원가입
    func signup(request: SignupRequest) async throws -> UserResponse {
        return try await apiClient.request(
            endpoint: "/api/auth/signup",
            method: "POST",
            body: request,
            responseType: UserResponse.self,
            requiresAuth: false
        )
    }
    
    // 로그인
    func login(email: String, password: String) async throws -> LoginResponse {
        let loginRequest = LoginRequest(email: email, password: password)
        let response = try await apiClient.request(
            endpoint: "/api/auth/login",
            method: "POST",
            body: loginRequest,
            responseType: LoginResponse.self,
            requiresAuth: false
        )
        
        // 토큰 저장
        TokenManager.shared.saveToken(response.token)
        
        return response
    }
    
    // 로그아웃
    func logout() {
        TokenManager.shared.deleteToken()
    }
    
    // 현재 로그인 상태 확인
    func isLoggedIn() -> Bool {
        return TokenManager.shared.hasToken()
    }
}
```

### 5. 사용 예시

```swift
// 회원가입
let authService = AuthService()

let signupRequest = SignupRequest(
    nickname: "user123",
    name: "홍길동",
    email: "user@example.com",
    password: "password123",
    dob: "1990-01-01",
    gender: "M",
    lastLocation: "서울"
)

do {
    let user = try await authService.signup(request: signupRequest)
    print("회원가입 성공: \(user.nickname ?? "")")
} catch {
    print("회원가입 실패: \(error)")
}

// 로그인
do {
    let response = try await authService.login(
        email: "user@example.com",
        password: "password123"
    )
    print("로그인 성공: \(response.user.nickname ?? "")")
    print("토큰: \(response.token)")
} catch {
    print("로그인 실패: \(error)")
}

// 인증이 필요한 API 호출 (예: 사용자 정보 조회)
class UserService {
    private let apiClient = APIClient.shared
    
    func getMyProfile() async throws -> UserResponse {
        return try await apiClient.request(
            endpoint: "/api/users/v1/1", // 실제로는 현재 사용자 ID 사용
            responseType: UserResponse.self,
            requiresAuth: true // 인증 필요
        )
    }
}
```

---

## ⚠️ 중요 사항

1. **기존 로그인 API 변경**: 
   - ❌ 기존: `POST /api/users/v1/login`
   - ✅ 새로운: `POST /api/auth/login`

2. **인증이 필요한 API**:
   - 대부분의 사용자 API (`/api/users/v1/*`)는 이제 인증이 필요합니다
   - 회원가입과 로그인만 인증 없이 접근 가능합니다

3. **토큰 보안**:
   - 토큰을 안전하게 저장하세요 (Keychain 권장)
   - 토큰을 URL이나 로그에 노출하지 마세요
   - HTTPS를 사용하는 프로덕션 환경에서는 반드시 HTTPS를 사용하세요

4. **에러 처리**:
   - 401 Unauthorized: 토큰이 없거나 만료됨 → 다시 로그인 필요
   - 400 Bad Request: 요청 데이터 유효성 검증 실패

---

## 🔄 마이그레이션 가이드

기존 코드를 사용 중이라면:

1. 로그인 엔드포인트 변경: `/api/users/v1/login` → `/api/auth/login`
2. 회원가입 엔드포인트 추가: `/api/auth/signup`
3. 모든 API 요청에 `Authorization` 헤더 추가
4. `LoginResponse` 모델 추가 (토큰 포함)

---

## 📞 문의

문제가 발생하면 백엔드 개발자에게 문의하세요.


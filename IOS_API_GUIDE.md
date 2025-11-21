# iOS Swift 앱과 백엔드 연동 가이드

이 문서는 Swift로 작성된 iOS 앱이 Spring Boot 백엔드 API와 통신하는 방법을 설명합니다.

## 🔧 백엔드 설정

### 1. 서버 시작
백엔드 서버를 시작하면 기본적으로 `http://localhost:8080`에서 실행됩니다.

```bash
cd Phi-backend
./mvnw spring-boot:run
```

### 2. iOS에서 접근 가능한 주소 확인
iOS 시뮬레이터나 실제 기기에서 접근하려면:

- **시뮬레이터**: `http://localhost:8080` 또는 `http://127.0.0.1:8080` 사용 가능
- **실제 기기**: Mac의 로컬 IP 주소를 사용해야 합니다
  - 터미널에서 `ifconfig | grep "inet "` 실행하여 IP 주소 확인
  - 예: `http://192.168.1.100:8080`

## 📡 API 엔드포인트

기본 URL: `http://YOUR_SERVER_IP:8080`

### 사용자 (User) API

#### 1. 모든 사용자 조회
```
GET /api/users/v1
```

#### 2. 특정 사용자 조회
```
GET /api/users/v1/{id}
```

#### 3. 사용자 생성
```
POST /api/users/v1
Content-Type: application/json

{
  "nickname": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "password": "password123",
  "dob": "1990-01-01",
  "gender": "M",
  "lastLocation": "37.5665,126.9780"
}
```

#### 4. 사용자 정보 수정
```
PUT /api/users/v1/{id}
Content-Type: application/json

{
  "nickname": "updated_nickname",
  "name": "홍길동",
  "email": "user@example.com"
}
```

#### 5. 사용자 삭제
```
DELETE /api/users/v1/{id}
```

#### 6. 로그인 (⚠️ 변경됨: JWT 토큰 사용)
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": { ... }
}
```

**⚠️ 중요**: 이제 JWT 토큰 기반 인증을 사용합니다. 자세한 내용은 `JWT_AUTH_GUIDE.md`를 참조하세요.

#### 7. 회원가입 (신규)
```
POST /api/auth/signup
Content-Type: application/json

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

**⚠️ 중요**: 인증이 필요한 API는 `Authorization: Bearer {token}` 헤더를 포함해야 합니다.

### 리포트 (Report) API

#### 1. 모든 리포트 조회
```
GET /api/reports/v1
```

#### 2. 특정 리포트 조회
```
GET /api/reports/v1/{id}
```

#### 3. 리포트 생성
```
POST /api/reports/v1
Content-Type: application/json

{
  "location": "37.5665,126.9780",
  "incidentClass": "Fire",
  "credibility": 80,
  "urgency": 90,
  "reportType": "USER",
  "description": "화재 발생",
  "incidentType": "FIRE",
  "incidentDescription": "빌딩에서 연기 발생"
}
```

**참고**: 
- 기존 사고에 리포트를 추가하려면 `incidentId` 제공
- 새로운 사고를 만들려면 `incidentType`과 `incidentDescription` 제공

#### 4. 특정 사고의 모든 리포트 조회
```
GET /api/reports/v1/incident/{incidentId}
```

#### 5. 리포트 삭제
```
DELETE /api/reports/v1/{id}
```

## 📱 Swift 코드 예시

### 1. API 클라이언트 생성

```swift
import Foundation

class APIClient {
    // 실제 사용 시에는 여기에 서버 IP 주소를 설정하세요
    private let baseURL = "http://localhost:8080"
    
    static let shared = APIClient()
    
    private init() {}
    
    // Generic HTTP Request 함수
    func request<T: Decodable>(
        endpoint: String,
        method: String = "GET",
        body: Encodable? = nil,
        responseType: T.Type
    ) async throws -> T {
        guard let url = URL(string: "\(baseURL)\(endpoint)") else {
            throw APIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let body = body {
            request.httpBody = try JSONEncoder().encode(body)
        }
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }
        
        guard (200...299).contains(httpResponse.statusCode) else {
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
}
```

### 2. 사용자 모델

```swift
// UserResponse.swift
struct UserResponse: Codable {
    let userId: Int64?
    let nickname: String?
    let name: String?
    let email: String?
    let credScore: Int?
    let userScore: Int?
    let dob: String? // LocalDate는 ISO 8601 문자열로 전송됨
    let gender: String?
    let lastLocation: String?
    let userCreatedAt: String?
}

// UserRequest.swift
struct UserRequest: Codable {
    let nickname: String?
    let name: String?
    let email: String?
    let password: String?
    let dob: String?
    let gender: String?
    let lastLocation: String?
}

// LoginRequest.swift
struct LoginRequest: Codable {
    let email: String
    let password: String
}
```

### 3. API 호출 예시

```swift
// 사용 예시
class UserService {
    private let apiClient = APIClient.shared
    
    // 로그인
    func login(email: String, password: String) async throws -> UserResponse {
        let loginRequest = LoginRequest(email: email, password: password)
        return try await apiClient.request(
            endpoint: "/api/users/v1/login",
            method: "POST",
            body: loginRequest,
            responseType: UserResponse.self
        )
    }
    
    // 사용자 생성
    func createUser(request: UserRequest) async throws -> UserResponse {
        return try await apiClient.request(
            endpoint: "/api/users/v1",
            method: "POST",
            body: request,
            responseType: UserResponse.self
        )
    }
    
    // 모든 사용자 조회
    func getAllUsers() async throws -> [UserResponse] {
        return try await apiClient.request(
            endpoint: "/api/users/v1",
            responseType: [UserResponse].self
        )
    }
    
    // 특정 사용자 조회
    func getUserById(_ id: Int64) async throws -> UserResponse {
        return try await apiClient.request(
            endpoint: "/api/users/v1/\(id)",
            responseType: UserResponse.self
        )
    }
}
```

### 4. 리포트 API 호출 예시

```swift
// ReportResponse.swift
struct ReportResponse: Codable {
    let reportId: Int64?
    let location: String?
    let incidentClass: String?
    let credibility: Int?
    let urgency: Int?
    let reportType: String?
    let incidentId: Int64?
    let timestamp: String?
    let description: String?
}

// ReportRequest.swift
struct ReportRequest: Codable {
    let location: String?
    let incidentClass: String?
    let credibility: Int?
    let urgency: Int?
    let reportType: String?
    let incidentId: Int64?
    let description: String?
    let incidentType: String?
    let incidentDescription: String?
}

// 사용 예시
class ReportService {
    private let apiClient = APIClient.shared
    
    // 리포트 생성
    func createReport(request: ReportRequest) async throws -> ReportResponse {
        return try await apiClient.request(
            endpoint: "/api/reports/v1",
            method: "POST",
            body: request,
            responseType: ReportResponse.self
        )
    }
    
    // 모든 리포트 조회
    func getAllReports() async throws -> [ReportResponse] {
        return try await apiClient.request(
            endpoint: "/api/reports/v1",
            responseType: [ReportResponse].self
        )
    }
}
```

## 🔒 iOS App Transport Security 설정

iOS는 기본적으로 HTTP 연결을 차단합니다. 개발 환경에서 HTTP를 사용하려면 `Info.plist`에 다음 설정을 추가하세요:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

**주의**: 프로덕션에서는 HTTPS를 사용하고 특정 도메인만 허용하도록 설정하세요.

## 🚀 실제 기기에서 테스트하는 방법

1. Mac과 iPhone이 같은 Wi-Fi 네트워크에 연결되어 있는지 확인
2. Mac의 로컬 IP 주소 확인:
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```
3. Swift 코드의 `baseURL`을 Mac의 IP 주소로 변경:
   ```swift
   private let baseURL = "http://192.168.1.100:8080"
   ```
4. 백엔드 서버가 실행 중인지 확인
5. 방화벽에서 8080 포트가 열려있는지 확인

## 📝 참고사항

- 모든 API는 JSON 형식으로 통신합니다
- 날짜는 ISO 8601 형식으로 전송됩니다 (예: "1990-01-01")
- 에러 응답은 적절한 HTTP 상태 코드를 반환합니다 (404, 400, 500 등)
- CORS는 이미 백엔드에서 설정되어 있어 iOS에서 바로 호출 가능합니다


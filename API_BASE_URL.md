# 프론트엔드용 API 기본 URL

## 🎯 호스트 주소

백엔드 서버는 다음 주소에서 실행됩니다:

### 개발 환경

**iOS 시뮬레이터용:**
```
http://localhost:8080
```

**실제 iOS 기기용:**
```
http://10.68.209.21:8080
```

> **참고**: Mac의 IP 주소는 네트워크 변경 시 바뀔 수 있습니다. IP 주소를 다시 확인하려면 터미널에서 `ifconfig | grep "inet " | grep -v 127.0.0.1` 실행하세요.

## 📱 Swift 코드에서 사용법

### 방법 1: 환경에 따라 자동 선택

```swift
struct APIConfig {
    #if targetEnvironment(simulator)
        // 시뮬레이터
        static let baseURL = "http://localhost:8080"
    #else
        // 실제 기기
        static let baseURL = "http://10.68.209.21:8080"
    #endif
}
```

### 방법 2: Build Configuration 사용

```swift
struct APIConfig {
    #if DEBUG
        static let baseURL = "http://localhost:8080"  // 개발용
    #else
        static let baseURL = "http://10.68.209.21:8080"  // 프로덕션용
    #endif
}
```

### 방법 3: 설정 파일로 관리 (권장)

`Config.plist` 파일 생성:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>APIBaseURL</key>
    <string>http://10.68.209.21:8080</string>
</dict>
</plist>
```

Swift 코드:
```swift
struct APIConfig {
    static var baseURL: String {
        guard let path = Bundle.main.path(forResource: "Config", ofType: "plist"),
              let dict = NSDictionary(contentsOfFile: path),
              let url = dict["APIBaseURL"] as? String else {
            return "http://localhost:8080" // 기본값
        }
        return url
    }
}
```

## 🔗 전체 API 엔드포인트 예시

기본 URL: `http://10.68.209.21:8080` (실제 기기 기준)

- 사용자 API: `http://10.68.209.21:8080/api/users/v1` (인증 필요)
- 리포트 API: `http://10.68.209.21:8080/api/reports/v1` (인증 필요)
- 회원가입: `http://10.68.209.21:8080/api/auth/signup` (인증 불필요)
- 로그인: `http://10.68.209.21:8080/api/auth/login` (인증 불필요)

**⚠️ 중요**: JWT 토큰 기반 인증을 사용합니다. 자세한 내용은 `JWT_AUTH_GUIDE.md`를 참조하세요.

## ⚠️ 중요 사항

1. **네트워크**: iOS 기기와 Mac이 같은 Wi-Fi 네트워크에 연결되어 있어야 합니다.
2. **방화벽**: Mac 방화벽에서 포트 8080이 열려있는지 확인하세요.
3. **서버 실행**: 백엔드 서버가 실행 중이어야 합니다.
   ```bash
   ./mvnw spring-boot:run
   ```
4. **App Transport Security**: 개발 환경에서는 `Info.plist`에 HTTP 허용 설정이 필요할 수 있습니다.

## 🧪 테스트 방법

터미널에서 서버 응답 확인:
```bash
curl http://10.68.209.21:8080/api/users/v1
```

iOS 앱에서 테스트:
```swift
let url = URL(string: "\(APIConfig.baseURL)/api/users/v1")!
// API 호출 코드...
```


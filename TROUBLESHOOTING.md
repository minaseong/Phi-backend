# "Could not reach server" 에러 해결 가이드

## 🔍 문제 진단

"Could not reach server" 에러는 프론트엔드가 백엔드 서버에 연결할 수 없다는 의미입니다.

## ✅ 확인 사항

### 1. 서버 실행 확인
```bash
# 서버가 실행 중인지 확인
lsof -i :8080

# 서버 실행 (실행 중이 아니라면)
./mvnw spring-boot:run
```

### 2. 현재 Mac IP 주소 확인
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**중요**: 실제 iOS 기기를 사용하는 경우, 프론트엔드 코드의 `baseURL`이 현재 Mac의 IP 주소와 일치해야 합니다.

### 3. 프론트엔드 URL 설정 확인

#### iOS 시뮬레이터 사용 시:
```swift
static let baseURL = "http://localhost:8080"
```

#### 실제 iOS 기기 사용 시:
```swift
// Mac의 현재 IP 주소로 변경 필요
static let baseURL = "http://[현재_Mac_IP]:8080"
// 예: "http://192.168.1.100:8080"
```

### 4. 네트워크 연결 확인

#### 같은 Wi-Fi 네트워크인지 확인
- Mac과 iPhone이 **같은 Wi-Fi**에 연결되어 있어야 합니다
- 서로 다른 네트워크면 연결 불가능

#### curl로 테스트
```bash
# localhost 테스트
curl http://localhost:8080/api/auth/signup

# Mac IP로 테스트 (실제 기기에서 접근할 때 사용하는 주소)
curl http://[Mac의_IP]:8080/api/auth/signup
```

### 5. 방화벽 확인

Mac 방화벽이 8080 포트를 차단하고 있을 수 있습니다.

**방화벽 설정 확인:**
1. 시스템 설정 → 네트워크 → 방화벽
2. 방화벽이 켜져 있다면, 터미널 앱에 인바운드 연결 허용 설정

**또는 방화벽에서 포트 열기:**
```bash
# 방화벽 상태 확인
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --getglobalstate

# 특정 포트 허용 (필요시)
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /usr/bin/java
```

### 6. App Transport Security 설정

iOS는 기본적으로 HTTP를 차단합니다. `Info.plist`에 다음 설정이 있는지 확인:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

## 🔧 해결 방법

### 방법 1: 프론트엔드 URL 수정

1. **현재 Mac IP 주소 확인**
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```

2. **프론트엔드 코드에서 baseURL 수정**
   - `AuthAPI.swift` 또는 API 설정 파일 찾기
   - `baseURL`을 현재 Mac IP로 변경
   - 예: `http://192.168.1.100:8080`

### 방법 2: 시뮬레이터 사용

실제 기기 대신 iOS 시뮬레이터를 사용하면 `localhost`로 접근 가능:

```swift
static let baseURL = "http://localhost:8080"
```

### 방법 3: 서버 재시작

```bash
# 서버 중지 (Ctrl+C)
# 서버 재시작
./mvnw spring-boot:run
```

## 🧪 테스트 방법

### 1. 서버가 응답하는지 확인
```bash
curl http://localhost:8080/api/auth/signup
```

### 2. 프론트엔드에서 사용하는 URL로 테스트
```bash
# 예: Mac IP가 192.168.1.100인 경우
curl http://192.168.1.100:8080/api/auth/signup
```

### 3. 회원가입 API 직접 테스트
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "testuser",
    "name": "테스트",
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 📱 프론트엔드 코드 확인 포인트

1. **baseURL이 올바른가?**
   - 시뮬레이터: `http://localhost:8080`
   - 실제 기기: `http://[Mac_IP]:8080`

2. **엔드포인트가 올바른가?**
   - 회원가입: `/api/auth/signup` (기존 `/api/users/v1/login` 아님!)

3. **에러 핸들링이 있는가?**
   - 네트워크 에러를 제대로 처리하고 있는지 확인

## 💡 빠른 체크리스트

- [ ] 서버가 실행 중인가? (`lsof -i :8080`)
- [ ] Mac과 iPhone이 같은 Wi-Fi에 연결되어 있는가?
- [ ] 프론트엔드 baseURL이 올바른가?
- [ ] 실제 기기 사용 시 Mac IP 주소가 맞는가?
- [ ] `Info.plist`에 HTTP 허용 설정이 있는가?
- [ ] curl로 서버에 접근 가능한가?

## 🆘 여전히 안 되면

1. **서버 로그 확인**: 서버 콘솔에 요청이 들어오는지 확인
   - 요청이 안 보이면 → 네트워크/URL 문제
   - 요청이 보이면 → 서버 내부 에러

2. **프론트엔드 네트워크 로그 확인**: Xcode의 Network Inspector 사용

3. **에러 메시지 확인**: 프론트엔드에서 받은 정확한 에러 메시지 확인


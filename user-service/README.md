# User Service

사용자 관리 및 인증을 담당하는 마이크로서비스입니다.

## 개발 환경 실행 방법

### 1. H2 인메모리 데이터베이스 사용 (권장)

MySQL 설치 없이 바로 실행할 수 있습니다:

```bash
# macOS/Linux에서 실행
./gradlew :user-service:bootRun --args='--spring.profiles.active=dev'

# Windows에서 실행
.\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=dev'
```

또는 IDE에서 실행할 때:
- VM Options: `-Dspring.profiles.active=dev`
- 또는 Program Arguments: `--spring.profiles.active=dev`

**⚠️ 중요:** IDE에서 단순히 우클릭 → Run을 하면 데이터베이스 설정이 없어서 오류가 발생합니다. 반드시 프로파일을 설정하세요:
- **개발환경:** `dev` 프로파일 (H2 데이터베이스)
- **운영환경:** `prod` 프로파일 (MySQL 데이터베이스)

### 2. MySQL 데이터베이스 사용 (운영환경)

실제 MySQL을 사용하려면:

1. **MySQL 설치 및 설정**
   ```bash
   # MySQL 설치 후 다음 명령어로 데이터베이스 생성
   mysql -u root -p
   CREATE DATABASE happy_maple_day_user;
   ```

2. **MySQL 사용자 설정**
   ```sql
   -- root 사용자 비밀번호를 'password'로 설정
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'password';
   FLUSH PRIVILEGES;
   ```

3. **애플리케이션 실행**
   ```bash
   # macOS/Linux
   ./gradlew :user-service:bootRun --args='--spring.profiles.active=prod'
   
   # Windows
   .\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=prod'
   ```

### 3. Docker를 이용한 MySQL 실행

```bash
# 프로젝트 루트에서 실행
docker-compose up -d
```

## API 엔드포인트

### 인증 관련
- `POST /api/user/signup` - 회원가입
- `POST /api/user/login` - 로그인
- `POST /api/user/logout` - 로그아웃
- `POST /api/user/refresh` - 토큰 갱신
- `POST /api/user/reset-password` - 비밀번호 재설정

### 사용자 설정 관련
- `PUT /api/user/main-character` - 본캐 변경
- `GET /api/user/settings` - 사용자 설정 조회
- `PUT /api/user/settings/privacy` - 개인정보 수집 동의 설정 수정
- `PUT /api/user/settings/weekly-reset` - 주간 초기화 설정 수정

## 환경 설정

### 개발 환경 (dev 프로파일)
- H2 인메모리 데이터베이스 사용
- H2 Console: http://localhost:8082/h2-console
- 자동 DDL 생성 (`create-drop`)
- 포트: 8082
- 디버그 로깅 활성화

### 운영 환경 (prod 프로파일)
- MySQL 데이터베이스 사용
- DDL 자동 업데이트 (`update`)
- 프로덕션 로깅 설정

### 기본 환경 (프로파일 없음)
- **⚠️ 경고:** 데이터베이스 설정이 없어서 실행 불가
- 반드시 `dev` 또는 `prod` 프로파일 지정 필요

## 테스트 실행

```bash
# macOS/Linux에서 모든 테스트 실행
./gradlew :user-service:test

# Windows에서 모든 테스트 실행
.\gradlew.bat :user-service:test

# 특정 테스트 클래스 실행 (macOS/Linux)
./gradlew :user-service:test --tests UserServiceTest

# 특정 테스트 클래스 실행 (Windows)
.\gradlew.bat :user-service:test --tests UserServiceTest
```

## 문제 해결

### MySQL 연결 오류
```
Access denied for user 'root'@'localhost' (using password: YES)
```

위 오류가 발생하면 **개발 환경 실행 방법 1번**을 사용하여 H2 데이터베이스로 실행하세요.

### Windows에서 Gradle Wrapper 오류
```
Error: Unable to access jarfile C:\Users\User\HappyMapleDay_BE\\gradle\wrapper\gradle-wrapper.jar
```

또는 PowerShell에서:
```
gradlew.bat : 'gradlew.bat' 용어가 cmdlet, 함수, 스크립트 파일 또는 실행할 수 있는 프로그램 이름으로 인식되지 않습니다.
```

**해결책 (우선순위 순):**

**🥇 가장 쉬운 방법: IDE 사용**
   - IntelliJ IDEA에서 `UserApplication.java` 실행
   - Run Configuration에서 VM Options: `-Dspring.profiles.active=dev`
   - 이 방법이 가장 확실하고 안전합니다!

**🥈 Gradle 직접 설치**
   ```cmd
   # Gradle 설치 후 (https://gradle.org/install/)
   gradle :user-service:bootRun --args='--spring.profiles.active=dev'
   ```

**🥉 Gradle Wrapper 재생성**
   ```cmd
   # 시스템에 Gradle이 설치되어 있다면
   gradle wrapper --gradle-version 8.14.2
   .\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=dev'
   ```

**🔧 기본 방법: gradlew.bat 사용**
   ```cmd
   .\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=dev'
   ```
   
   **중요:** Windows PowerShell에서는 반드시 `.\gradlew.bat`로 실행해야 합니다 (앞에 `.\` 필수)

2. **관리자 권한으로 실행** (필요한 경우)
   - 명령 프롬프트를 관리자 권한으로 실행

3. **Java 환경 변수 확인**
   ```cmd
   java -version
   echo %JAVA_HOME%
   ```

4. **경로에 공백이 있는 경우**
   - 프로젝트를 공백이 없는 경로로 이동 (예: `C:\dev\HappyMapleDay_BE`)
   - 사용자 이름에 공백이 있으면 문제 발생 가능 (예: `C:\Users\User Name\`)

7. **Gradle Wrapper 파일 문제**
   - 다음 명령어로 gradle wrapper 재생성:
   ```cmd
   gradle wrapper --gradle-version 8.14.2
   ```

8. **Git LFS 문제 (바이너리 파일)**
   - gradle-wrapper.jar가 제대로 클론되지 않았을 수 있음
   - 다음 명령어로 확인:
   ```cmd
   dir gradle\wrapper\gradle-wrapper.jar
   ```
   - 파일 크기가 0이거나 매우 작으면 재다운로드 필요

9. **대안 해결책**
   - 시스템에 Gradle을 직접 설치하고 `gradle` 명령어 사용:
   ```cmd
   gradle :user-service:bootRun --args='--spring.profiles.active=local'
   ```

5. **IDE에서 실행 (권장)**
   
   **IntelliJ IDEA:**
   - `UserApplication.java` 파일에서 우클릭 → Run (첫 실행)
   - 실행 후 상단 툴바에서 Run Configuration 선택 → Edit Configurations
   - 또는 Run → Edit Configurations... 메뉴 선택
   - VM Options 필드에 `-Dspring.profiles.active=dev` 입력
   - 또는 Program Arguments 필드에 `--spring.profiles.active=dev` 입력
   - Apply → OK → 다시 Run
   
   **Eclipse:**
   - `UserApplication.java` 파일에서 우클릭 → Run As → Java Application
   - Run Configurations에서 Arguments 탭 → VM arguments에 `-Dspring.profiles.active=dev` 추가

6. **Gradle 빌드 먼저 실행**
   ```cmd
   .\gradlew.bat build
   .\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=dev'
   ```

### 포트 충돌
기본 포트는 8082입니다. 포트를 변경하려면:

```bash
# macOS/Linux (개발환경)
./gradlew :user-service:bootRun --args='--spring.profiles.active=dev --server.port=8090'

# Windows (개발환경)
.\gradlew.bat :user-service:bootRun --args='--spring.profiles.active=dev --server.port=8090'
``` 
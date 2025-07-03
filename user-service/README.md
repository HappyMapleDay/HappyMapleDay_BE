# User Service

사용자 관리 및 인증을 담당하는 마이크로서비스입니다.

## 개발 환경 실행 방법

### 1. H2 인메모리 데이터베이스 사용 (권장)

MySQL 설치 없이 바로 실행할 수 있습니다:

```bash
# 프로젝트 루트에서 실행
./gradlew :user-service:bootRun --args='--spring.profiles.active=local'
```

또는 IDE에서 실행할 때:
- VM Options: `-Dspring.profiles.active=local`
- 또는 Program Arguments: `--spring.profiles.active=local`

### 2. MySQL 데이터베이스 사용

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
   ./gradlew :user-service:bootRun
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

### 개발 환경 (local 프로파일)
- H2 인메모리 데이터베이스 사용
- H2 Console: http://localhost:8082/h2-console
- 자동 DDL 생성 (`create-drop`)
- 포트: 8082

### 운영 환경 (default 프로파일)
- MySQL 데이터베이스 사용
- DDL 자동 생성 비활성화

## 테스트 실행

```bash
# 모든 테스트 실행
./gradlew :user-service:test

# 특정 테스트 클래스 실행
./gradlew :user-service:test --tests UserServiceTest
```

## 문제 해결

### MySQL 연결 오류
```
Access denied for user 'root'@'localhost' (using password: YES)
```

위 오류가 발생하면 **개발 환경 실행 방법 1번**을 사용하여 H2 데이터베이스로 실행하세요.

### 포트 충돌
기본 포트는 8082입니다. 포트를 변경하려면:

```bash
./gradlew :user-service:bootRun --args='--server.port=8090'
``` 
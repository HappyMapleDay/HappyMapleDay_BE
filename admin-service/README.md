# Admin Service

관리자용 통계 수집 및 조회 서비스입니다. 다른 마이크로서비스들로부터 데이터를 수집하여 집계하고, 통계 데이터를 제공합니다.

## 주요 기능

### 1. 배치 작업 (자동 스케줄링)
- **유저 통계**: 매일 자정 1시 실행
- **보스 격파 횟수**: 매주 목요일 0시 실행
- **전투력 평균**: 매주 목요일 0시 30분 실행
- **아이템 드롭**: 매주 목요일 1시 실행
- **아이템 판매가**: 매주 목요일 1시 30분 실행

### 2. 데이터 수집
- User Service: 가입 유저 통계
- Settlement Service: 보스/아이템 관련 통계
- Boss Service: 보스/아이템 메타데이터 보강

### 3. 통계 조회 API
- 가입 유저 수 현황
- 보스별 격파 횟수 (주차별)
- 보스별 전투력 평균
- 보스별 물욕템 드롭 현황
- 보스별 물욕템 판매가 현황

## 기술 스택
- Spring Boot 3.x
- Spring Data JPA
- MySQL
- OpenFeign (마이크로서비스 통신)
- Spring Security (Basic Auth)
- Spring Scheduler (배치 스케줄링)

## API 엔드포인트

### 배치 관리 API
- `POST /api/admin/batch/execute-all` - 전체 배치 일괄 실행
- `POST /api/admin/batch/execute` - 개별 배치 수동 실행
- `GET /api/admin/batch/status` - 배치 상태 조회
- `GET /api/admin/batch/history` - 배치 실행 이력 조회

### 통계 조회 API
- `GET /api/admin/metrics/users` - 유저 통계 조회
- `GET /api/admin/metrics/bosses/kills` - 보스 격파 횟수 조회
- `GET /api/admin/metrics/bosses/combat-power` - 보스 전투력 평균 조회
- `GET /api/admin/metrics/bosses/items/drops` - 아이템 드롭 현황 조회
- `GET /api/admin/metrics/bosses/items/prices` - 아이템 판매가 현황 조회

## 환경 설정

### application.yml
```yaml
server:
  port: 8087

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/happy_maple_day_admin
    username: root
    password: password
  
  security:
    user:
      name: admin
      password: admin123

services:
  user-service:
    url: http://localhost:8082
  boss-service:
    url: http://localhost:8081
  settlement-service:
    url: http://localhost:8084
```

## 보안
- Basic Authentication 적용
- 기본 계정: `admin` / `admin123`
- Health Check 엔드포인트만 공개

## 모니터링
- Spring Actuator 활성화
- Prometheus 메트릭 노출
- 배치 작업 상태 추적

## 프로젝트 구조
```
admin-service/
├── client/              # FeignClient (외부 서비스 호출)
├── config/              # 설정 클래스
├── controller/          # REST API 컨트롤러
├── dto/
│   ├── external/        # 외부 서비스 DTO
│   ├── request/         # 요청 DTO
│   └── response/        # 응답 DTO
├── entity/              # JPA 엔티티 (7개 테이블)
├── enums/               # Enum 클래스
├── repository/          # JPA Repository
├── scheduler/           # 스케줄러
└── service/             # 비즈니스 로직
```

## 데이터베이스 테이블
1. `user_metrics` - 유저 통계
2. `boss_kill_metrics` - 보스 격파 횟수
3. `boss_combat_power_metrics` - 보스 전투력 통계
4. `item_drop_metrics` - 아이템 드롭 통계
5. `item_price_metrics` - 아이템 가격 통계
6. `batch_job_status` - 배치 작업 상태 (최신)
7. `batch_execution_history` - 배치 실행 이력 (전체)

## 실행 방법
```bash
./gradlew :admin-service:bootRun
```

## 개발 참고사항
- 배치 작업은 트랜잭션 단위로 실행
- 실패 시 자동 롤백 및 이력 기록
- 외부 서비스 연동 실패 시 재시도 없음 (수동 재실행 필요)
- 주차별 데이터는 목요일 날짜로 정규화


# 🍁 HappyMapleDay - Multi-Module MSA Project

메이플스토리 보스 관리 시스템을 위한 마이크로서비스 아키텍처 프로젝트입니다.

## 📋 프로젝트 구조

```
happy-maple-day/                     # 루트 프로젝트
├── boss-service/                    # 보스 관리 서비스
├── user-service/                    # 사용자 관리 서비스
├── character-service/               # 캐릭터 관리 서비스
├── settlement-service/              # 정산 관리 서비스
├── recommendation-service/          # 추천 서비스

├── admin-service/                  # 관리자 서비스
├── gateway/                        # API Gateway
└── common/                         # 공통 라이브러리
```

## 🏢 서비스별 기능 및 특화점

| 서비스 | 포트 | 주요 기능 | 기술 특화점 |
|-------|------|-----------|-------------|
| **API Gateway** | 8080 | • 통합 API 진입점<br>• 라우팅 & 로드밸런싱<br>• CORS 처리 | • Spring Cloud Gateway<br>• JWT 검증<br>• 중앙화된 인증 |
| **Boss Service** | 8081 | • 보스/물욕템 마스터 관리<br>• 프리셋 관리<br>• 기본 수익 계산 | • 마스터 데이터 관리<br>• 참조 데이터 최적화 |
| **User Service** | 8082 | • 회원가입/로그인<br>• 본캐 변경<br>• JWT 인증 | • JWT 인증<br>• 보안 강화 |
| **Character Service** | 8083 | • 캐릭터 추가/삭제<br>• Nexon API 연동<br>• 캐릭터 상세정보 조회 | • 외부 API 캐싱<br>• Rate Limiting<br>• Circuit Breaker |
| **Settlement Service** | 8084 | • 주간 보스 현황 관리<br>• 보돌 완료 처리<br>• 수익 정산 | • 복잡한 집계 로직<br>• 트랜잭션 관리<br>• 배치 처리 |
| **Recommendation Service** | 8085 | • 캐릭터 능력 분석<br>• 최적화 알고리즘 실행<br>• 추천 결과 제공 | • 조합 최적화 알고리즘<br>• 성능 최적화<br>• 결과 캐싱 |

| **Admin Service** | 8087 | • 보스 현황 모니터링<br>• 물욕템 습득 현황<br>• 통계 대시보드 | • 관리자 권한 관리<br>• 모니터링<br>• 로그 수집 |

## 🚀 빠른 시작

### 1. 전체 프로젝트 빌드
```bash
./gradlew build
```

### 2. 개별 서비스 실행
```bash
# API Gateway 실행 (필수)
./gradlew :gateway:bootRun

# Boss Service 실행
./gradlew :boss-service:bootRun

# User Service 실행  
./gradlew :user-service:bootRun

# Character Service 실행
./gradlew :character-service:bootRun

# Settlement Service 실행
./gradlew :settlement-service:bootRun

# Recommendation Service 실행
./gradlew :recommendation-service:bootRun



# Admin Service 실행
./gradlew :admin-service:bootRun
```

### 3. 서비스 확인
- **API Gateway**: http://localhost:8080 (통합 진입점)
- Boss Service: http://localhost:8081
- User Service: http://localhost:8082  
- Character Service: http://localhost:8083
- Settlement Service: http://localhost:8084
- Recommendation Service: http://localhost:8085

- Admin Service: http://localhost:8087

## 🗄️ 데이터베이스 구조

각 서비스는 독립적인 데이터베이스를 사용합니다:

- `happy_maple_day_master` - Boss Service (마스터 데이터)
- `happy_maple_day_user` - User Service (사용자 정보)
- `happy_maple_day_character` - Character Service (캐릭터 정보)
- `happy_maple_day_settlement` - Settlement Service (정산 데이터)
- `happy_maple_day_recommendation` - Recommendation Service (추천 결과)

- `happy_maple_day_admin` - Admin Service (관리자 데이터)

## 🔧 필수 요구사항

- **Java**: 17+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (캐싱용)
- **Gradle**: 8.0+

## 📡 서비스 간 통신

### API Gateway를 통한 통합 접근
- 모든 클라이언트는 Gateway(8080)를 통해 접근
- Gateway가 요청을 적절한 서비스로 라우팅
- JWT 토큰 검증 중앙화

### Feign Client를 통한 내부 통신
- 공통 모듈(`common`)에 각 서비스별 Feign Client 인터페이스 정의
- 각 서비스는 필요한 다른 서비스의 Client를 주입받아 사용

### API Contract First 접근
- 모든 서비스의 API 인터페이스를 우선 정의
- Mock Server를 활용한 독립적 개발 지원

## 🐳 OCI Micro Instance Docker 최적화

OCI micro 인스턴스(6GB RAM, ARM64 아키텍처)에서 Docker 빌드 시 메모리 부족 문제를 해결하기 위한 최적화가 적용되어 있습니다.

### 주요 최적화 내용

#### 1. ARM64 아키텍처 호환성
- **플랫폼 명시**: `--platform=linux/arm64` 설정
- **ARM64 이미지**: `eclipse-temurin:17-jdk-alpine` ARM64 버전 사용
- **Health Check**: `wget` 대신 `curl` 사용 (ARM64 호환성)
- **모든 서비스**: ARM64 플랫폼으로 빌드 및 실행

#### 2. Dockerfile 최적화
- **메모리 제한**: JVM 힙 메모리를 512MB로 제한
- **빌드 최적화**: `--max-workers=1`, `--parallel=false` 설정
- **레이어 최소화**: 불필요한 패키지 설치 제거
- **멀티스테이지 빌드**: 최종 이미지 크기 최소화

#### 3. 빌드 스크립트 사용
```bash
# 최적화된 빌드 스크립트 실행 (ARM64 호환)
./build-optimized.sh

# 또는 개별 서비스 빌드
docker build --platform linux/arm64 -t happymapleday-boss:latest -f ./boss-service/Dockerfile . --memory=512m --memory-swap=1g
```

#### 4. 시스템 최적화
```bash
# Docker 데몬 메모리 제한
sudo dockerd --storage-driver=overlay2 --max-concurrent-downloads=1 --max-concurrent-uploads=1

# 빌드 전 메모리 정리
docker system prune -f
docker volume prune -f
```

#### 5. 환경변수 설정
```bash
# Gradle 메모리 제한
export GRADLE_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

# Docker 빌드 최적화
export DOCKER_BUILDKIT=1
export BUILDKIT_PROGRESS=plain
```

### 빌드 순서 권장사항
1. **boss-service** (의존성 최소)
2. **user-service**
3. **character-service**
4. **settlement-service**
5. **recommendation-service**
6. **admin-service**
7. **gateway** (의존성 최대)

### 문제 해결
- **빌드 실패 시**: `docker system prune -af`로 전체 정리 후 재시도
- **메모리 부족 시**: 다른 서비스 중지 후 빌드
- **타임아웃 시**: `--timeout 600` 옵션 추가
- **ARM64 호환성**: 모든 이미지가 ARM64 플랫폼으로 빌드되는지 확인

## 🛠️ 개발 가이드

### 1. 새로운 서비스 추가
```bash
# 1. 디렉토리 생성
mkdir new-service/src/main/java/com/happymapleday/newservice

# 2. settings.gradle에 모듈 추가
include 'new-service'

# 3. build.gradle 생성
# 4. Application.java 생성
# 5. application.yml 설정
# 6. Gateway 라우팅 추가
```

### 2. 공통 라이브러리 수정
- `common` 모듈에서 공통 DTO, Client 인터페이스 관리
- 변경 시 모든 서비스에 영향을 미치므로 신중하게 수정

### 3. 서비스별 특화 개발
- 각 서비스는 독립적으로 개발 가능
- 다른 서비스와의 통신은 Feign Client 사용

## 🚦 다음 단계

1. **실제 API 구현**: 각 서비스별 REST API 구현
2. **Docker 컨테이너화**: 각 서비스별 Dockerfile 작성
3. **CI/CD 파이프라인**: GitHub Actions 구성
4. **모니터링**: Prometheus + Grafana 구성
5. **로그 수집**: ELK Stack 구성

## 💡 팁

- **Gateway 우선 실행**: 클라이언트는 Gateway를 통해 접근
- 각 서비스는 독립적으로 실행 가능
- 개발 시 필요한 서비스만 실행하여 리소스 절약 가능
- 공통 모듈 변경 시 전체 빌드 필요
- 서비스 간 API 변경 시 계약(Contract) 우선 협의
- **OCI micro 인스턴스**: 메모리 제한으로 인한 빌드 실패 시 최적화된 빌드 스크립트 사용 
#!/bin/bash

# OCI Micro Instance용 최적화된 Docker 빌드 스크립트
# 메모리 부족 문제를 해결하기 위한 설정

echo "🚀 OCI Micro Instance용 최적화 Docker 빌드 시작"

# Docker 빌드 시 메모리 제한 설정
export DOCKER_BUILDKIT=1
export BUILDKIT_PROGRESS=plain

# 시스템 메모리 확인
echo "📊 시스템 메모리 상태 확인..."
free -h
echo ""

# Docker 데몬 메모리 제한 설정
echo "🔧 Docker 데몬 메모리 제한 설정..."
sudo systemctl stop docker
sudo dockerd --storage-driver=overlay2 --max-concurrent-downloads=1 --max-concurrent-uploads=1 &
sleep 5

# 빌드 순서 (의존성 순서대로)
SERVICES=("common" "boss-service" "user-service" "character-service" "settlement-service" "recommendation-service" "admin-service" "gateway")

for service in "${SERVICES[@]}"; do
    echo "🏗️  $service 빌드 시작..."
    
    # 빌드 전 메모리 정리
    docker system prune -f
    
    # 빌드 시도
    if docker build -t "happymapleday-${service}:latest" -f "./${service}/Dockerfile" . --memory=512m --memory-swap=1g; then
        echo "✅ $service 빌드 성공!"
    else
        echo "❌ $service 빌드 실패. 재시도..."
        
        # 추가 메모리 정리 후 재시도
        docker system prune -af
        docker volume prune -f
        
        if docker build -t "happymapleday-${service}:latest" -f "./${service}/Dockerfile" . --memory=512m --memory-swap=1g; then
            echo "✅ $service 빌드 재시도 성공!"
        else
            echo "❌ $service 빌드 최종 실패"
            exit 1
        fi
    fi
    
    echo ""
done

echo "🎉 모든 서비스 빌드 완료!"
echo "📋 빌드된 이미지 목록:"
docker images | grep happymapleday

# Docker 데몬 정상화
sudo pkill dockerd
sudo systemctl start docker
sudo systemctl status docker

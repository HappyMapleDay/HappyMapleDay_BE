# Runtime stage
FROM --platform=linux/arm64 eclipse-temurin:17-jre

# Set timezone
ENV TZ=Asia/Seoul

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# GitHub Actions 워크플로우에서 빌드한 JAR 파일을 복사
COPY user-service/build/libs/*.jar app.jar

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application with memory constraints
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:MaxMetaspaceSize=128m", "-jar", "app.jar"]
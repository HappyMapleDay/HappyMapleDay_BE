# Multi-stage build optimized for micro instances (ARM64 compatible)
FROM --platform=linux/arm64 eclipse-temurin:17-jdk AS builder

# Set working directory
WORKDIR /workspace

# Install necessary packages
RUN apt-get update && apt-get install -y bash curl && rm -rf /var/lib/apt/lists/*

# Copy only essential files first
COPY gradlew ./
COPY gradle/ gradle/
COPY build.gradle ./
COPY settings.gradle ./
COPY boss-service/build.gradle ./boss-service/
COPY common/ ./common/

# Make gradlew executable
RUN chmod +x ./gradlew

# Set JVM options for micro instances
ENV GRADLE_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

# Download dependencies first (for better caching)
RUN ./gradlew :boss-service:dependencies --no-daemon --stacktrace

# Copy source code
COPY boss-service/src/ ./boss-service/src/

    # Build with memory optimization
    RUN ./gradlew :boss-service:bootJar -x test --no-daemon --stacktrace \
        --max-workers=1 \
        --no-parallel \
        --no-configure-on-demand

# Runtime stage
FROM --platform=linux/arm64 eclipse-temurin:17-jre

# Set timezone
ENV TZ=Asia/Seoul

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy built jar
COPY --from=builder /workspace/boss-service/build/libs/*.jar app.jar

# Expose port
EXPOSE 8081

# Simple health check (ARM64 compatible)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application with memory constraints
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:MaxMetaspaceSize=128m", "-jar", "app.jar"]

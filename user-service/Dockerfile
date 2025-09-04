# Multi-stage build optimized for micro instances (ARM64 compatible)
FROM --platform=linux/arm64 eclipse-temurin:17-jdk AS builder

# Set working directory
WORKDIR /workspace

# Install necessary packages
RUN apt-get update && apt-get install -y bash curl && rm -rf /var/lib/apt/lists/*

# Copy only necessary files for build
COPY gradlew ./
COPY gradle/ gradle/
COPY build.gradle ./
COPY settings.gradle ./
COPY user-service/build.gradle ./user-service/
COPY common/ ./common/

# Make gradlew executable
RUN chmod +x ./gradlew

# Set JVM options for micro instances
ENV GRADLE_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

# Download dependencies first (for better caching)
RUN ./gradlew :user-service:dependencies --no-daemon --stacktrace

# Copy source code
COPY user-service/src/ ./user-service/src/

# Build with memory optimization
RUN ./gradlew :user-service:bootJar -x test --no-daemon --stacktrace \
    --max-workers=1 \
    --parallel=false \
    --configure-on-demand=false \
    && sh -c 'JAR_FILE=$(ls user-service/build/libs | grep -v plain | head -n1) && cp user-service/build/libs/$JAR_FILE app.jar'

# Runtime stage
FROM --platform=linux/arm64 eclipse-temurin:17-jre

# Set timezone
ENV TZ=Asia/Seoul

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -m appuser

# Set working directory
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /workspace/app.jar app.jar

# Change ownership to app user
RUN chown appuser:appgroup app.jar

# Switch to app user
USER appuser

# Expose port
EXPOSE 8082

# Health check (ARM64 compatible)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application with memory constraints
ENTRYPOINT ["sh", "-c", "java -Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m -jar app.jar"]



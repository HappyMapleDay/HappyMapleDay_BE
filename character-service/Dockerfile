# Multi-stage build for optimization
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /workspace

# Copy only necessary files for build
COPY gradlew ./
COPY gradle/ gradle/
COPY build.gradle ./
COPY settings.gradle ./
COPY character-service/build.gradle ./character-service/
COPY common/ ./common/

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies first (for better caching)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY character-service/src/ ./character-service/src/

# Build the application
RUN ./gradlew :character-service:bootJar -x test --no-daemon \
    && sh -c 'JAR_FILE=$(ls character-service/build/libs | grep -v plain | head -n1) && cp character-service/build/libs/$JAR_FILE app.jar'

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set timezone
ENV TZ=Asia/Seoul

# Create app user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /workspace/app.jar app.jar

# Change ownership to app user
RUN chown appuser:appgroup app.jar

# Switch to app user
USER appuser

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]



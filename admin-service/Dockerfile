FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew && ./gradlew :admin-service:bootJar -x test --no-daemon \
  && sh -c 'JAR_FILE=$(ls admin-service/build/libs | grep -v plain | head -n1) && cp admin-service/build/libs/$JAR_FILE app.jar'

FROM eclipse-temurin:17-jre-alpine
ENV TZ=Asia/Seoul
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar
EXPOSE 8087
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]



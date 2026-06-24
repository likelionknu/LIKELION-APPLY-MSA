# ---------- build ----------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# ---------- runtime ----------
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app -u 1000
WORKDIR /app
COPY --from=builder --chown=1000:1000 /workspace/build/libs/*.jar app.jar
USER 1000:1000
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
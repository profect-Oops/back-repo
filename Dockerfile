# === Build Stage ===
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set work directory
WORKDIR /app

# Gradle 캐시 디렉토리 설정 (효율적인 캐시 사용)
ENV GRADLE_USER_HOME=/cache/gradle

# Copy Gradle wrapper and download dependencies first (이 단계에서 캐시 활용)
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./

# Gradle 의존성 다운로드 (의존성 설치만 먼저 실행, 캐시 활용)
RUN ./gradlew build -x test --no-daemon || return 0

# Copy the full source code
COPY . .

# Build JAR file
RUN ./gradlew clean build -x test

# === Run Stage ===
FROM eclipse-temurin:17-jre-alpine AS runtime

# Set work directory
WORKDIR /app

# Copy only the built JAR from build stage
COPY --from=builder /app/build/libs/oops-1.0-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
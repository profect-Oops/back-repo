# Gradle이 포함된 JDK 베이스 이미지
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉터리 설정
WORKDIR /app

# Gradle 설정 복사
COPY gradle/ ./gradle/
COPY gradlew .
COPY build.gradle settings.gradle ./

# gradlew 파일의 실행 권한 설정
RUN chmod +x gradlew

# 의존성 캐시 다운로드
RUN ./gradlew build -x test --no-daemon || return 0

# 애플리케이션 전체 파일 복사
COPY . .

# JAR 파일 빌드
RUN ./gradlew clean build -x test

# 실제 실행 이미지 설정
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/oops-1.0-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
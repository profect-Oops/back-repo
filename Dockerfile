# Base image 변경: slim 사용
FROM openjdk:17-slim

# 필수 패키지 설치 (ping, netcat, nslookup, curl)
RUN apt-get update && apt-get install -y iputils-ping netcat dnsutils curl && rm -rf /var/lib/apt/lists/*

# Set work directory
WORKDIR /app

# Copy application jar file
COPY build/libs/oops-1.0-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

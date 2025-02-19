# === Build Stage ===
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set work directory
WORKDIR /app

# Copy source code
COPY . .

# Build JAR file (Gradle)
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

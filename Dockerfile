# --- Step 1: Build stage (Maven compilation) ---
FROM maven:3.9-eclipse-temurin-23 AS builder
WORKDIR /build

# Copy Maven configuration files to prefetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the executable JAR (tests are skipped to speed up the image build)
COPY src ./src
RUN mvn package -DskipTests

# --- Step 2: Final image (lightweight runtime) ---
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Create a non-root user for better container security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR produced by the build stage
COPY --from=builder /build/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
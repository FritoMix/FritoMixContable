# ═══════════════════════════════════════════════════════════════
#  STAGE 1: Build Spring Boot Jar with Maven
# ═══════════════════════════════════════════════════════════════
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src ./src
RUN mvn package -DskipTests

# ═══════════════════════════════════════════════════════════════
#  STAGE 2: Lightweight JRE Runtime
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built artifact from Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

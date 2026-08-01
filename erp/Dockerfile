# ── Build stage ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q clean package -DskipTests

# ── Runtime stage ───────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -m -u 1001 appuser && chown -R appuser /app
USER appuser

COPY --from=build /app/target/erp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

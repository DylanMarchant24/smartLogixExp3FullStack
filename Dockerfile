# ==============================================================================
# Dockerfile genérico para microservicios Java Spring Boot de SmartLogix.
# Se usa el mismo Dockerfile para los 13 módulos backend (discovery-server,
# api-gateway, bff, y los 10 ms-*), pasando el nombre del módulo como build arg.
#
# Uso:
#   docker build --build-arg MODULE=ms-inventario -t smartlogix/ms-inventario .
# ==============================================================================

# ── Etapa 1: compilación con Maven ────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE
WORKDIR /app
COPY ${MODULE}/pom.xml .
RUN mvn dependency:go-offline -B
COPY ${MODULE}/src ./src
RUN mvn clean package -DskipTests -q

# ── Etapa 2: imagen final, solo runtime ────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

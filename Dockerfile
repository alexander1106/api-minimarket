# ---- build ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src src
RUN ./mvnw -q -B -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home appuser && mkdir -p /app/uploads && chown -R appuser /app
COPY --from=build /workspace/target/api-0.0.1-SNAPSHOT.jar app.jar
USER appuser
ENV UPLOAD_DIR=/app/uploads
# Contenedores pequenos (512 MB-1 GB): la JVM usa hasta el 75 % de la memoria asignada
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 CMD wget -qO- "http://localhost:${PORT:-8080}/actuator/health" || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]

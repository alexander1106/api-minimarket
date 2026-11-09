FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon -q quarkusBuild -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/quarkus-app/ ./
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]

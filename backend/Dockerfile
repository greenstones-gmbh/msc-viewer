# === Backend Build Stage ===
FROM eclipse-temurin:21-alpine AS backend-build
WORKDIR /app

COPY ./pom.xml ./
COPY ./mvnw ./
COPY ./.mvn .mvn
RUN ./mvnw dependency:go-offline

COPY ./src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-alpine
ARG JAR_FILE=target/*.jar
COPY --from=backend-build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=docker"]
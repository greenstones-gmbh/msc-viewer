
# === Backend Build Stage ===
FROM eclipse-temurin:21-alpine AS backend-build
WORKDIR /app

COPY backend/pom.xml ./
COPY backend/mvnw ./
COPY backend/.mvn .mvn
RUN ./mvnw dependency:go-offline

COPY backend/src ./src
RUN ./mvnw package -DskipTests


# === Frontend Build Stage ===
FROM node:18 AS frontend-build
WORKDIR /frontend


COPY frontend/package.json frontend/package-lock.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build


# === Final Image ===
FROM alpine:latest
WORKDIR /app

RUN apk add --no-cache openjdk21 neo4j nginx bash curl

COPY --from=backend-build /app/target/*.jar app.jar
COPY --from=frontend-build /frontend/build /usr/share/nginx/html/msc-viewer


COPY ./backend/src/test/responses /tmp/msc-viewer


COPY docker/nginx.default.conf /etc/nginx/http.d/default.conf

COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80 8080 7474 7687
ENTRYPOINT ["/entrypoint.sh"]



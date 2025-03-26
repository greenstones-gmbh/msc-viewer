#!/bin/sh

echo "Starting Neo4j..."
neo4j start &

sleep 4

echo "Starting NGINX..."
nginx &

echo "Starting Spring Boot application..."
exec java -jar /app/app.jar --spring.profiles.active=docker

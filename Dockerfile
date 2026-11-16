# Fase 1: Compilación con Maven y JDK 8 (idéntico al entorno local)
FROM maven:3.9-eclipse-temurin-8-alpine AS build
WORKDIR /app

# Actualizar certificados CA en Alpine para evitar fallos SSL
RUN apk add --no-cache ca-certificates && update-ca-certificates

COPY pom.xml .
COPY src ./src

# Forzar TLSv1.2 en Maven
RUN mvn clean package -DskipTests -Dhttps.protocols=TLSv1.2 -Dmaven.wagon.http.ssl.insecure=false

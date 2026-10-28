# Fase 1: Compilación con Maven y JDK 17
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Se usa mvn instalado en la imagen en lugar de ./mvnw
RUN mvn clean package -DskipTests

# Fase 2: Ejecución en JRE 8
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]

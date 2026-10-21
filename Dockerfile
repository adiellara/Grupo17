# Fase 1: Compilación con Maven y OpenJDK 8
FROM maven:3.8.8-eclipse-temurin-8-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Imagen liviana para ejecutar la aplicación
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

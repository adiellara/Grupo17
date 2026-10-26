# Fase 1: Compilación con Maven y Java 8
FROM maven:3.8.8-eclipse-temurin-8-alpine AS build
WORKDIR /app

# Copia los archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compila y empaqueta el proyecto omitiendo los tests
RUN mvn clean package -DskipTests

# Fase 2: Ejecución del JAR
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Copia el ejecutable .jar generado desde la fase de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto
EXPOSE 8080

# Ejecuta la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

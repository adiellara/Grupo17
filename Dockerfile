# Fase 1: Compilación usando Maven con JDK 17 (soporta cifrado TLS moderno)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia los archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compila y empaqueta la aplicación
RUN mvn clean package -DskipTests

# Fase 2: Ejecución en JRE 8
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Copia el archivo .jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

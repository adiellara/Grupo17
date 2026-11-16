FROM maven:3.9-eclipse-temurin-8-alpine AS build
WORKDIR /app

# Actualizar certificados raíz en Alpine para evitar errores SSL/handshake con Maven Central
RUN apk add --no-cache ca-certificates && update-ca-certificates

# Copiar primero el pom.xml y descargar dependencias (aprovecha la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B -Dhttps.protocols=TLSv1.2

# Copiar el código fuente y empaquetar el proyecto
COPY src ./src
RUN mvn clean package -DskipTests -Dhttps.protocols=TLSv1.2


# Fase 2: Ejecución ligera con JRE 8

FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Actualizar certificados en el runtime para conexiones seguras (ej. MySQL 8+ en la nube)
RUN apk add --no-cache ca-certificates && update-ca-certificates

# Copiar el archivo .jar compilado desde la Fase 1
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto estándar (Render asigna la variable PORT automáticamente si se configura)
EXPOSE 8080

# Limitar la memoria RAM de la JVM para evitar reinicios por falta de memoria en el plan gratuito de Render (512MB)
ENTRYPOINT ["java", "-Xmx350m", "-Xms128m", "-Dhttps.protocols=TLSv1.2", "-jar", "app.jar"]

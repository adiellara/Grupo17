# FASE 1: Compilación con Maven y JDK 8 
FROM maven:3.9-eclipse-temurin-8-alpine AS build
WORKDIR /app

# Actualizar certificados raíz
RUN apk add --no-cache ca-certificates && update-ca-certificates

# aprovecha la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B -Dhttps.protocols=TLSv1.2

# empaquetar el proyecto
COPY src ./src
RUN mvn clean package -DskipTests -Dhttps.protocols=TLSv1.2


# FASE 2: Ejecución ligera con JRE 
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Instalar certificados 
RUN apk add --no-cache ca-certificates tzdata && update-ca-certificates

# Copiar el archivo .jar 
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto 
EXPOSE 8080

# Optimización estricta de RAM 
ENTRYPOINT ["java", \
  "-Xmx300m", \
  "-Xms128m", \
  "-XX:MaxMetaspaceSize=110m", \
  "-Xss256k", \
  "-Duser.timezone=America/El_Salvador", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dhttps.protocols=TLSv1.2", \
  "-jar", "app.jar"]

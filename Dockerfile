# Usa una imagen oficial de Java 8 con Maven integrado
FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app

# Copia los archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compila el proyecto generando el archivo .jar
RUN mvn clean package -DskipTests

# Fase de ejecución con Java 8 JRE
FROM openjdk:8-jre-slim
WORKDIR /app

# Copia el ejecutable desde la fase de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

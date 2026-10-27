# Etapa de compilación
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw
COPY src ./src

RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xms128m", "-Xmx384m", "-jar", "/app/app.jar"]

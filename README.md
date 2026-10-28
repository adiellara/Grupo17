# Grupo 17

Aplicación web para gestión de pedidos, usuarios, productos y pagos en una cafetería/restaurante, desarrollada con Spring Boot y Thymeleaf.

## Tecnologías
- Java 17
- Spring Boot 2.7.8
- Spring MVC
- Spring Data JPA
- MySQL
- Maven
- Thymeleaf

## Requisitos previos
- Java 17
- Maven 3.9+
- MySQL 8+

## Ejecutar en local
```bash
cp .env.example .env
# Ajusta los valores de .env antes de arrancar
./mvnw spring-boot:run
```

## Variables de entorno
La app usa estas variables:

```bash

```

Importante:
- No subas credenciales reales al repositorio.
- En Render y Clever Cloud debes configurarlas desde el panel del servicio.

## Despliegue en Render
1. Conecta este repositorio a Render.
2. Crea un servicio Web con `Docker`.
3. Usa el archivo `render.yaml` incluido en este repositorio.
4. Configura las variables de entorno en Render:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `PORT`
5. Haz deploy.

## Despliegue en Clever Cloud
1. Crea una base de datos MySQL en Clever Cloud.
2. En la app, agrega las variables de entorno:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `PORT`
3. Usa el `Dockerfile` del proyecto o el build nativo.


## Estructura del proyecto
```bash
src/
├── main/
│   ├── java/
│   │   └── com/ues/edu
│   ├── resources/
│   │   ├── static/
│   │   ├── templates/
│   │   └── application.properties
│   └── test/
├── Dockerfile
├── render.yaml
├── .env.example
├── pom.xml
└── README.md
```

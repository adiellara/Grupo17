# SIPAGO — Sistema de Gestión de Pedidos y Pagos

**Grupo 17 · Universidad de El Salvador (UES)**

Aplicación web para la gestión de usuarios, productos, pedidos y pagos en el contexto de una cafetería o restaurante. El sistema utiliza una arquitectura web basada en Spring MVC, persistencia con Spring Data JPA y vistas renderizadas en el servidor mediante Thymeleaf.

El proyecto tiene como objetivo centralizar las operaciones principales del negocio en una plataforma web, facilitando la administración de la información y el seguimiento de los pedidos.

---

## Tabla de contenido

- [1. Descripción general](#1-descripción-general)
- [2. Alcance funcional](#2-alcance-funcional)
- [3. Arquitectura](#3-arquitectura)
- [4. Tecnologías](#4-tecnologías)
- [5. Requisitos previos](#5-requisitos-previos)
- [6. Instalación y ejecución local](#6-instalación-y-ejecución-local)
- [7. Configuración](#7-configuración)
- [8. Base de datos](#8-base-de-datos)
- [9. Pruebas](#9-pruebas)
- [10. Despliegue](#10-despliegue)
- [11. Seguridad](#11-seguridad)
- [12. Solución de problemas](#12-solución-de-problemas)
- [13. Estructura del proyecto](#13-estructura-del-proyecto)
- [14. Equipo de desarrollo](#14-equipo-de-desarrollo)

---

## 1. Descripción general

SIPAGO es una aplicación web desarrollada como proyecto académico del Grupo 17 de la Universidad de El Salvador.

La plataforma está orientada a la administración de las operaciones de una cafetería o restaurante, integrando la gestión de información de usuarios, el catálogo de productos, las órdenes y los pagos.

Su desarrollo utiliza el patrón Modelo-Vista-Controlador (MVC), con separación de responsabilidades entre los controladores, la lógica de negocio, las entidades de persistencia y las interfaces de usuario.

### Objetivos del sistema

- Centralizar la información operativa del negocio.
- Facilitar la administración del catálogo de productos y los pedidos.
- Proporcionar interfaces diferenciadas según las operaciones del sistema.
- Mantener la información persistente mediante una base de datos relacional.
- Facilitar la instalación, ejecución y despliegue de la aplicación.

## 2. Alcance funcional

El sistema contempla los siguientes módulos funcionales. Las operaciones concretas disponibles dependen de la implementación vigente del proyecto.

| Módulo | Responsabilidad |
|---|---|
| Usuarios y autenticación | Gestión de usuarios y acceso al sistema. |
| Catálogo de productos | Consulta y administración de los productos disponibles. |
| Pedidos | Registro, consulta y gestión de órdenes. |
| Pagos | Gestión de la información asociada a los pagos. |
| Administración | Acceso a las funciones administrativas del sistema. |

**Nota:** las funciones como filtros en tiempo real, bloqueo de cuentas inactivas, cálculo automático de órdenes y restricciones de acceso deben documentarse como funcionalidades implementadas únicamente cuando estén verificadas en el código.

## 3. Arquitectura

SIPAGO utiliza una arquitectura web basada en Spring MVC y Thymeleaf.

```text
┌──────────────────────────────────────┐
│              Usuario                 │
└──────────────────┬───────────────────┘
                   │ HTTP
                   ▼
┌──────────────────────────────────────┐
│       Capa de presentación           │
│       Thymeleaf, HTML y CSS          │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Controladores MVC              │
│       Spring MVC                     │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Lógica de negocio              │
│       Servicios y validaciones       │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Persistencia                   │
│       Spring Data JPA                │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Base de datos MySQL             │
└──────────────────────────────────────┘
```

El diagrama representa la separación lógica prevista para la aplicación; la estructura exacta debe contrastarse con los paquetes y componentes existentes.

### Responsabilidades por capa

- **Presentación:** interfaces HTML renderizadas mediante Thymeleaf y recursos estáticos.
- **Controladores:** recepción de solicitudes HTTP y coordinación del flujo de la aplicación.
- **Servicios:** encapsulación de reglas de negocio, validaciones y operaciones.
- **Persistencia:** acceso a los datos mediante repositorios de Spring Data JPA.
- **Base de datos:** almacenamiento persistente de la información del sistema.

## 4. Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje de programación | Java 8  |
| Framework principal | Spring Boot 2.7.8 |
| Capa web | Spring MVC |
| Persistencia ORM | Spring Data JPA |
| Motor de plantillas | Thymeleaf |
| Base de datos | MySQL 8+ |
| Gestión de dependencias | Maven |
| Contenedorización | Docker |
| Despliegue en la nube | Render y Clever Cloud |

**Compatibilidad:** Spring Boot 2.7.8 admite Java 8 como versión mínima. La versión efectiva de Java debe coincidir con la configuración del proyecto, especialmente con `pom.xml`, el entorno de desarrollo y el `Dockerfile`.

## 5. Requisitos previos

Antes de ejecutar el sistema, instala o verifica los siguientes componentes:

- **JDK:** versión compatible con el proyecto.
- **MySQL:** versión 8 o compatible con la configuración de la aplicación.
- **Maven:** versión 3.9 o superior, si se utiliza una instalación local.
- **Git:** para clonar el repositorio.
- **Docker:** únicamente si se ejecuta o despliega mediante contenedores.

Se recomienda utilizar el Maven Wrapper incluido en el repositorio para ejecutar los comandos sin depender de una instalación global de Maven.

Verifica las herramientas disponibles:

```bash
java -version
./mvnw -version
```

En Windows, utiliza:

```powershell
java -version
.\mvnw.cmd -version
```

## 6. Instalación y ejecución local

### 6.1. Obtener el código fuente

Clona el repositorio y accede al directorio del proyecto:

```bash
git clone <URL_DEL_REPOSITORIO>
cd <DIRECTORIO_DEL_PROYECTO>
```

Sustituye los marcadores por la URL y el nombre reales del repositorio.

### 6.2. Preparar la base de datos

Inicia MySQL y crea la base de datos que utilizará la aplicación, salvo que la configuración existente gestione su creación automáticamente.

Ejemplo:

```sql
CREATE DATABASE sipago_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

El nombre `sipago_db` es ilustrativo. Debe coincidir con el nombre configurado en la URL de conexión de la aplicación.

### 6.3. Configurar las variables de entorno

Copia la plantilla de configuración:

```bash
cp .env.example .env
```

Edita `.env` con los datos de conexión de tu entorno local.

**Importante:** Spring Boot no carga automáticamente cualquier archivo `.env`. Las variables deben estar disponibles en el entorno del proceso, configuradas en el IDE o cargadas mediante una herramienta compatible.

### 6.4. Compilar el proyecto

```bash
./mvnw clean package
```

Para omitir temporalmente las pruebas durante una compilación de diagnóstico:

```bash
./mvnw clean package -DskipTests
```

No se recomienda omitir las pruebas como práctica habitual de integración o entrega.

### 6.5. Iniciar la aplicación

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Si el arranque finaliza correctamente, consulta las rutas de acceso definidas por los controladores de la aplicación.

Por defecto, Spring Boot utiliza el puerto `8080`, salvo que la configuración o la variable `PORT` establezcan otro valor.

## 7. Configuración

La configuración de conexión a la base de datos debe mantenerse fuera del código fuente cuando incluya credenciales o datos específicos del entorno.

### Variables de entorno

| Variable | Descripción | Ejemplo local |
|---|---|---|
| `PORT` | Puerto HTTP de la aplicación, si está configurado para utilizar esta variable. | `8080` |
| `SPRING_DATASOURCE_URL` | URL JDBC de la base de datos. | `jdbc:mysql://localhost:3306/sipago_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de MySQL. | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña del usuario de MySQL. | No publicar |

### Plantilla `.env.example`

Utiliza una plantilla sin credenciales reales:

```dotenv
PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/sipago_db
SPRING_DATASOURCE_USERNAME=usuario_local
SPRING_DATASOURCE_PASSWORD=cambiar_esta_contrasena
```

Los nombres de las variables deben coincidir con las propiedades que consume la aplicación.

Si `application.properties` utiliza `${PORT:8080}`, por ejemplo, Spring Boot podrá utilizar `8080` como valor predeterminado. Si la aplicación no referencia `PORT`, tendrás que configurar el puerto mediante la propiedad correspondiente.

Para MySQL, cualquier parámetro adicional de la URL JDBC debe añadirse según los requisitos del controlador y del entorno de ejecución.

### Configuración mediante `application.properties`

Una configuración típica compatible con las variables anteriores es:

```properties
server.port=${PORT:8080}

spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

Este fragmento es una referencia de configuración, no una indicación de que debas reemplazar todo tu archivo existente. Conserva las demás propiedades requeridas por el proyecto, incluidos el controlador JDBC, JPA y las migraciones, si existen.

## 8. Base de datos

La aplicación utiliza MySQL como sistema de gestión de bases de datos y Spring Data JPA para el acceso a los datos.

Antes de ejecutar el proyecto, verifica:

1. Que el servidor MySQL esté disponible.
2. Que la base de datos exista o que el mecanismo de inicialización esté correctamente configurado.
3. Que las credenciales tengan los permisos necesarios.
4. Que las entidades JPA y el esquema de la base de datos sean compatibles.
5. Que el usuario de la aplicación no utilice permisos administrativos innecesarios en producción.

Si el proyecto incluye scripts SQL, archivos de inicialización o migraciones, deben ejecutarse según el procedimiento definido en el repositorio.

No se debe asumir que JPA crea todas las tablas automáticamente: ese comportamiento depende de la configuración de Hibernate y del mecanismo de inicialización utilizado.

## 9. Pruebas

El proyecto puede compilarse y probarse con Maven.

Ejecutar las pruebas:

```bash
./mvnw test
```

Compilar y empaquetar:

```bash
./mvnw clean package
```

Las pruebas deben cubrir, según el alcance implementado:

- Validaciones de las operaciones principales.
- Reglas de negocio de pedidos y productos.
- Persistencia y consultas a la base de datos.
- Comportamiento de los controladores.
- Restricciones de acceso y autenticación, si están implementadas.

La existencia de estos objetivos no implica que ya haya pruebas para todos ellos. Consulta `src/test/` para identificar la cobertura efectiva.

## 10. Despliegue

La aplicación contempla dos alternativas de despliegue: Render y Clever Cloud. Ambas requieren una configuración compatible con el proyecto y una base de datos accesible desde el entorno de ejecución.

### 10.1. Render

Si el repositorio incluye un `Dockerfile` y un `render.yaml` válidos para la aplicación:

1. Publica el repositorio en un proveedor Git compatible.
2. Crea un servicio web en Render.
3. Configura el servicio para utilizar Docker.
4. Revisa el archivo `render.yaml` y confirma que sus rutas y parámetros correspondan al proyecto.
5. Configura las variables de entorno del servicio.
6. Proporciona una URL JDBC y credenciales válidas para la base de datos.
7. Ejecuta el despliegue y revisa los registros de inicio.

Variables que deben verificarse:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `PORT`, si la configuración de la aplicación la utiliza.

**Base de datos:** no presupongas que el servicio web de Render incluye una instancia MySQL. Debes disponer de una base de datos compatible y accesible desde el servicio.

### 10.2. Clever Cloud

1. Prepara una instancia MySQL compatible con la aplicación.
2. Crea o configura el servicio de ejecución de la aplicación.
3. Selecciona el método de compilación y despliegue compatible con el repositorio: Docker o una configuración Java/Maven admitida.
4. Configura las variables de entorno.
5. Verifica la conectividad entre la aplicación y MySQL.
6. Despliega la aplicación y revisa los registros de ejecución.

Comprueba especialmente la URL JDBC, las credenciales, el puerto de escucha y la compatibilidad entre el entorno de ejecución y la versión de Java del proyecto.

## 11. Seguridad

Las credenciales y los datos de conexión deben gestionarse de forma segura.

- No publiques archivos `.env` con credenciales reales.
- Mantén `.env.example` libre de secretos.
- Verifica que `.env` esté excluido mediante `.gitignore`.
- No almacenes contraseñas de usuarios en texto plano.
- Utiliza mecanismos apropiados de hash de contraseñas y validación de acceso.
- Aplica controles de autorización en las operaciones administrativas y sensibles.
- No concedas privilegios innecesarios al usuario de MySQL.
- Evita registrar contraseñas, tokens o información sensible en los logs.
- Utiliza HTTPS en los entornos publicados y protege las credenciales de despliegue.

Si existe un usuario administrativo inicial, documenta el mecanismo de creación, la forma segura de establecer sus credenciales y el procedimiento para cambiarlas. No publiques contraseñas predeterminadas reutilizables en producción.

## 12. Solución de problemas

| Problema | Posibles causas | Qué comprobar |
|---|---|---|
| Error de conexión a MySQL | Servidor detenido, URL incorrecta o credenciales inválidas. | Estado de MySQL, URL JDBC y variables de entorno. |
| Error de autenticación en MySQL | Usuario o contraseña incorrectos, permisos insuficientes. | Credenciales y privilegios de la cuenta. |
| Puerto ocupado | Otro proceso utiliza el puerto configurado. | Puerto de la aplicación y procesos activos. |
| Error de compilación | JDK incompatible o dependencias no resueltas. | `java -version`, Maven Wrapper y `pom.xml`. |
| Plantillas no encontradas | Ruta incorrecta o vistas ausentes. | Controladores y directorio `templates/`. |
| Tablas inexistentes | Esquema no inicializado o estrategia JPA incompatible. | Scripts SQL, migraciones y configuración de Hibernate. |
| Despliegue fallido | Variables ausentes, imagen Docker incorrecta o base de datos inaccesible. | Logs del proveedor, configuración y conectividad. |

Para diagnosticar problemas, conserva el mensaje de error completo y revisa los logs antes de modificar la configuración.

## 13. Estructura del proyecto

La siguiente estructura es orientativa. Ajusta los nombres de paquetes y archivos para que coincidan con el repositorio real.

```text
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ues/edu/
│   │   │       ├── config/
│   │   │       ├── controllers/
│   │   │       ├── count/
│   │   │       ├── entities/
│   │   │       ├── loginCredentials/
│   │   │       ├── repositories/
│   │   │       └── services/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── .env.example
├── .gitignore
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
├── render.yaml
└── README.md
```

### Descripción de los directorios

| Ruta | Responsabilidad |
|---|---|
| `src/main/java/` | Código fuente Java. |
| `config/` | Configuración e inicialización de la aplicación, si corresponde. |
| `controllers/` | Controladores de Spring MVC. |
| `entities/` | Entidades de persistencia JPA. |
| `repositories/` | Repositorios para el acceso a datos. |
| `services/` | Servicios y lógica de negocio. |
| `resources/static/` | CSS, JavaScript, imágenes y otros recursos estáticos. |
| `resources/templates/` | Plantillas HTML de Thymeleaf. |
| `src/test/` | Pruebas automatizadas. |
| `pom.xml` | Dependencias, plugins y configuración de Maven. |
| `Dockerfile` | Definición de la imagen de la aplicación. |
| `render.yaml` | Configuración declarativa de Render, si está implementada. |
| `.env.example` | Plantilla de variables de entorno. |

## 14. Equipo de desarrollo

**Grupo 17 — Universidad de El Salvador (UES)**

- **Carlos Alexander De León:** Módulo Pagos y Tracking.
- **Adiel René Lara Valenzuela:** Módulo de Ordenes y Ticket.
- **Josue Alberto Rivera Coreas:** Módulo de Productos y Usuarios.

Los módulos indicados representan la distribución de responsabilidades del equipo; su alcance específico debe contrastarse con la versión entregada del sistema.

---

## Licencia

No se especifica una licencia de distribución en este documento. Si el proyecto se publicará para reutilización externa, incorpora una licencia explícita y el archivo correspondiente cuando el equipo haya definido sus condiciones de uso.

---

**SIPAGO · Grupo 17 · Universidad de El Salvador**

Sistema de Gestión de Pedidos y Pagos.


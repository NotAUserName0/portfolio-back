# 🌐 Portfolio Backend API

> **API RESTful desarrollada con Spring Boot 3 y Java 21 para la gestión, publicación y administración del portafolio profesional.**  
> Proporciona servicios para la gestión de proyectos, perfil, enlaces de contacto, almacenamiento seguro de imágenes multimedia, autenticación basada en JWT y defensas avanzadas contra ataques comunes (DDoS, Path Traversal, inyecciones y enumeración de usuarios).

---

## 📑 Tabla de Contenidos

1. [🛠️ Stack Tecnológico y Especificaciones](#-stack-tecnológico-y-especificaciones)
2. [📋 Requisitos Previos del Sistema](#-requisitos-previos-del-sistema)
3. [⚙️ Variables de Entorno y Configuración](#-variables-de-entorno-y-configuración)
4. [🚀 Guía de Instalación y Despliegue](#-guía-de-instalación-y-despliegue)
   - [Opción A: Ejecución Local con Maven](#opción-a-ejecución-local-con-maven)
   - [Opción B: Despliegue con Docker (Recomendada)](#opción-b-despliegue-con-docker-recomendada)
   - [Opción C: Despliegue con Docker Compose](#opción-c-despliegue-con-docker-compose)
5. [📡 Catálogo de Endpoints (API Reference)](#-catálogo-de-endpoints-api-reference)
6. [🛡️ Medidas de Seguridad Implementadas](#-medidas-de-seguridad-implementadas)
7. [📂 Estructura del Proyecto](#-estructura-del-proyecto)
8. [📄 Licencia y Autores](#-licencia-y-autores)

---

## 🛠️ Stack Tecnológico y Especificaciones

| Componente | Tecnología | Versión | Descripción |
|:---|:---|:---|:---|
| **Lenguaje** | Java | 21 (LTS) | Versión de Java requerida para la compilación y ejecución. |
| **Framework Base** | Spring Boot | 3.x / 4.x | Núcleo del backend (WebMVC, Data JPA, Security, Validation). |
| **Seguridad & Auth** | Spring Security + JJWT | 0.12.3 | Autenticación stateless mediante tokens JWT y cifrado BCrypt. |
| **Mapeo de Datos** | ModelMapper | 3.2.0 | Conversión automática entre entidades JPA y DTOs. |
| **Base de Datos** | MariaDB / MySQL | 10.x / 8.x | Motor relacional para la persistencia de datos. |
| **Driver BD** | MariaDB Java Client | 3.5.x | Conector JDBC de alto rendimiento. |
| **Contenedorización** | Docker | Multi-stage | Imagen final ligera basada en `eclipse-temurin:21-jre-alpine`. |
| **Puerto por Defecto** | HTTP | `8081` | Puerto configurado para atender peticiones web. |

---

## 📋 Requisitos Previos del Sistema

Antes de iniciar el proyecto, asegúrate de tener instalado:

1. **Java Development Kit (JDK):** Versión 21 o superior ([Descargar Eclipse Temurin](https://adoptium.net/)).
2. **Docker Engine y Docker Compose:** (Opcional pero recomendado si vas a desplegar en contenedores).
3. **Servidor de Base de Datos:** MariaDB (versión 10.6+) o MySQL (versión 8.0+).
4. **Git:** Para clonar y versionar el repositorio.

---

## ⚙️ Variables de Entorno y Configuración

La aplicación lee su configuración de [application.properties](src/main/resources/application.properties) y permite sobreescribir cualquier parámetro mediante variables de entorno del sistema operativo o archivos `.env`.

### Catálogo de Variables

| Variable de Entorno | Valor por Defecto | Descripción | Ejemplo Producción |
|:---|:---|:---|:---|
| `SERVER_PORT` | `8081` | Puerto HTTP donde corre el backend | `8081` |
| `SPRING_DATASOURCE_URL` | `jdbc:mariadb://localhost:8080/portfolio` | URL de conexión JDBC hacia MariaDB/MySQL | `jdbc:mariadb://db.midominio.com:3306/portfolio` |
| `SPRING_DATASOURCE_USERNAME` | `springRoot` | Usuario con permisos en la base de datos | `db_portfolio_user` |
| `SPRING_DATASOURCE_PASSWORD` | `12345` | Contraseña del usuario de base de datos | `ClaveSuperSegura_2026!` |
| `ADMIN_DEFAULT_USERNAME` | `admin` | Usuario administrador inicial creado en el primer arranque | `admin` |
| `ADMIN_DEFAULT_PASSWORD` | `12345` | Contraseña del usuario administrador inicial | `AdminPasswordFuerte_#987` |
| `ADMIN_DEFAULT_SECRET` | *Clave interna hardcodeada* | Secreto HMAC-SHA256 para firmar los tokens JWT (mínimo 256 bits) | `MiSecretoJWTSuperLargoYComplejo2026AlfabetoXYZ` |
| `ADMIN_DEFAULT_EXPIRATION` | `3600000` (1 hora) | Tiempo de validez del token JWT en milisegundos | `86400000` (24 horas) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Lista separada por comas de dominios frontend autorizados | `https://miportfolio.com,https://admin.miportfolio.com` |
| `UPLOAD_DIR` | `upload/` | Directorio en disco para almacenar imágenes subidas | `upload/` |
| `RATE_LIMIT_AUTH_MAX` | `10` | Límite de peticiones permitidas por IP en `/auth` | `5` |
| `RATE_LIMIT_AUTH_WINDOW` | `60` | Ventana de tiempo en segundos para el contador de peticiones | `60` |

### Plantilla de Archivo `.env` Recomendada
Crea un archivo `.env` en la raíz del proyecto para centralizar tus variables locales o de producción:

```env
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/portfolio
SPRING_DATASOURCE_USERNAME=springRoot
SPRING_DATASOURCE_PASSWORD=TuPasswordSeguro123

# Credenciales de Administrador Inicial
ADMIN_DEFAULT_USERNAME=admin
ADMIN_DEFAULT_PASSWORD=TuPasswordAdmin2026!

# Seguridad JWT
ADMIN_DEFAULT_SECRET=ClaveSecretaParaTokensJWT2026AlfabetoCompletoXYZABC12345
ADMIN_DEFAULT_EXPIRATION=86400000

# CORS (Frontend permitido)
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://miportfolio.com

# Almacenamiento
UPLOAD_DIR=upload/
```

---

## 🚀 Guía de Instalación y Despliegue

### Opción A: Ejecución Local con Maven

1. **Crear la Base de Datos:**  
   Accede a tu gestor MariaDB/MySQL y ejecuta:
   ```sql
   CREATE DATABASE IF NOT EXISTS portfolio;
   ```

2. **Compilar el proyecto:**
   ```bash
   # En Windows
   ./mvnw.cmd clean package -DskipTests

   # En Linux / macOS
   ./mvnw clean package -DskipTests
   ```

3. **Ejecutar la aplicación:**
   ```bash
   # Opción directa con Maven
   ./mvnw spring-boot:run

   # O ejecutando el archivo JAR generado
   java -jar target/porfolio-0.0.1-SNAPSHOT.jar
   ```

La aplicación iniciará en `http://localhost:8081`.

---

### Opción B: Despliegue con Docker (Recomendada)

El proyecto incluye un [Dockerfile](Dockerfile) multi-stage optimizado que compila y genera una imagen ligera con Alpine Linux.

1. **Construir la imagen de Docker:**
   ```bash
   docker build -t portfolio-backend:1.0.0 .
   ```

2. **Ejecutar el contenedor con persistencia de archivos (`-v`):**
   > [!IMPORTANT]
   > El parámetro `-v ./upload:/app/upload` es **obligatorio** para que las fotos y recursos multimedia subidos no se eliminen si el contenedor se reinicia o se actualiza.

   ```bash
   docker run -d \
     --name portfolio-api \
     -p 8081:8081 \
     -v ./upload:/app/upload \
     --env-file .env \
     portfolio-backend:1.0.0
   ```

3. **Verificar que el contenedor esté corriendo:**
   ```bash
   docker ps
   docker logs -f portfolio-api
   ```

---

### Opción C: Despliegue con Docker Compose

Si deseas levantar la base de datos MariaDB y la API de Spring Boot al mismo tiempo, crea un archivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  database:
    image: mariadb:10.11
    container_name: portfolio-mariadb
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword123
      MYSQL_DATABASE: portfolio
      MYSQL_USER: springRoot
      MYSQL_PASSWORD: springpassword123
    ports:
      - "3306:3306"
    volumes:
      - mariadb_data:/var/lib/mysql
    networks:
      - portfolio-net

  backend:
    build: .
    container_name: portfolio-backend
    restart: always
    depends_on:
      - database
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://database:3306/portfolio
      SPRING_DATASOURCE_USERNAME: springRoot
      SPRING_DATASOURCE_PASSWORD: springpassword123
      ADMIN_DEFAULT_USERNAME: admin
      ADMIN_DEFAULT_PASSWORD: AdminSecurePassword2026!
      ADMIN_DEFAULT_SECRET: MiSecretoJWTSuperSeguroDebeTenerMasDe256BitsXYZABC123
      CORS_ALLOWED_ORIGINS: http://localhost:5173,http://localhost:3000
      UPLOAD_DIR: upload/
    ports:
      - "8081:8081"
    volumes:
      - ./upload:/app/upload
    networks:
      - portfolio-net

networks:
  portfolio-net:
    driver: bridge

volumes:
  mariadb_data:
```

Para arrancar todo:
```bash
docker compose up -d --build
```

---

## 📡 Catálogo de Endpoints (API Reference)

### 1. Diagnóstico y Monitoreo

| Método | Endpoint | Acceso | Descripción |
|:---|:---|:---|:---|
| `GET` | `/liveness` | Público | Comprueba si el servicio está activo y respondiendo. |

---

### 2. Autenticación

| Método | Endpoint | Acceso | Payload (JSON) | Descripción |
|:---|:---|:---|:---|:---|
| `POST` | `/auth` | Público *(Rate Limited)* | `{"username": "...", "password": "..."}` | Inicia sesión y devuelve un token Bearer JWT. |

---

### 3. Portafolio Público y Privado

| Método | Endpoint | Acceso | Formato | Descripción |
|:---|:---|:---|:---|:---|
| `GET` | `/getPortfolio` | Público | JSON | Retorna toda la información del portafolio (perfil, proyectos, enlaces) para el frontend. |
| `GET` | `/portfolio` | Requiere JWT | JSON | Obtiene la información del portafolio autenticado. |
| `POST` | `/portfolio` | Requiere JWT | `multipart/form-data` | Crea el portafolio cargando simultáneamente datos JSON y archivos multimedia. |
| `PUT` | `/portfolio/{id}` | Requiere JWT | `multipart/form-data` | Actualiza un portafolio existente y reemplaza selectivamente imágenes. |
| `DELETE` | `/portfolio/{id}` | Requiere JWT | - | Elimina el portafolio y sus dependencias en cascada. |

#### Parámetros Multipart para `POST /portfolio` y `PUT /portfolio/{id}`:
* `portfolioData` (Parte JSON obligatoria): Objeto `PortfolioDto` serializado.
* `profilePic` (Archivo opcional): Foto de perfil (`MultipartFile`).
* `proyect` (Lista opcional de archivos): Imágenes asociadas a cada proyecto (`List<MultipartFile>`).
* `linksIcon` (Lista opcional de archivos): Íconos para los enlaces generales del portafolio.
* `icon` (Lista opcional de archivos): Íconos para enlaces internos dentro de proyectos.

---

### 4. Acceso a Archivos Estáticos (Imágenes)

| Método | Endpoint | Acceso | Descripción |
|:---|:---|:---|:---|
| `GET` | `/upload/{nombre_archivo}` | Público | Permite al navegador/frontend visualizar imágenes subidas (perfil, proyectos, íconos). |

---

## 🛡️ Medidas de Seguridad Implementadas

La aplicación incorpora protecciones alineadas a los estándares de **OWASP API Security**:

1. **Protección Anti-DDoS y Fuerza Bruta (Rate Limiting):**
   * El componente `RateLimitingFilter` supervisa las peticiones al endpoint `/auth`.
   * Bloquea ráfagas masivas que intenten saturar la CPU mediante el hashing de contraseñas (`BCrypt`), retornando código HTTP `429 Too Many Requests` cuando se supera el límite configurado.

2. **Prevención de Path Traversal y Subida Segura de Archivos:**
   * El servicio reutilizable `FileStorageService` descarta el nombre de archivo enviado por el cliente (`file.getOriginalFilename()`) y genera nombres aleatorios criptográficamente seguros mediante `UUID.randomUUID()`.
   * Aplica una lista blanca estricta de tipos MIME permitidos (`image/jpeg`, `image/png`, `image/webp`, `image/gif`, `image/svg+xml`) y valida que la ruta normalizada final resida estrictamente dentro de la carpeta `upload/`.

3. **Prevención de Enumeración de Usuarios:**
   * En caso de credenciales inválidas, el servicio de autenticación devuelve una respuesta idéntica `401 Unauthorized` con el mensaje genérico `"Invalid credentials"`, tanto si el usuario no existe como si la contraseña es errónea.

4. **Cabeceras HTTP de Seguridad:**
   * `X-Frame-Options: DENY`: Evita ataques de secuestro de clics (*Clickjacking*) impidiendo incrustar la API en iframes.
   * `X-Content-Type-Options: nosniff`: Impide que los navegadores interpreten archivos como tipos MIME ejecutables diferentes a los declarados.

5. **Validación de Entradas (Bean Validation):**
   * Los DTOs de entrada están protegidos mediante anotaciones de validación de Jakarta (`@NotBlank`, `@Size`, etc.) y controlados con `@Valid` en cada endpoint para descartar payloads corruptos o manipulados.

6. **Control de Acceso Cruzado (CORS):**
   * Restringe el consumo de la API exclusivamente a los orígenes autorizados configurados en `CORS_ALLOWED_ORIGINS`.

---

## 📂 Estructura del Proyecto

```
back/
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/porfolio/
│   │   │   ├── config/              # Configuraciones (Seguridad, CORS, ModelMapper, Recursos)
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── ModelMapperConfig.java
│   │   │   │   ├── PasswordParserConfig.java
│   │   │   │   ├── ResoursesConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controllers/         # Controladores REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── PortfolioController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                 # Data Transfer Objects con validaciones
│   │   │   ├── models/              # Entidades JPA (Portfolio, User, ProjectForm, LinkForm)
│   │   │   ├── repository/          # Interfaces Spring Data JPA
│   │   │   ├── service/             # Servicios de lógica de negocio y JWT
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── portfolio/PortfolioService.java
│   │   │   │   └── user/AuthService.java
│   │   │   └── utils/               # Componentes transversales y filtros
│   │   │       ├── ApiResponse.java
│   │   │       └── Components/
│   │   │           ├── FileStorageService.java   # Servicio reutilizable de subida segura
│   │   │           ├── GlobalExceptionHandler.java # Manejo global de errores
│   │   │           ├── JwtAuthFilter.java        # Filtro de autenticación JWT
│   │   │           └── RateLimitingFilter.java   # Filtro anti-DDoS / Rate Limiter
│   │   └── resources/
│   │       └── application.properties # Configuración por defecto de la aplicación
│   └── test/                        # Pruebas unitarias y de integración
├── upload/                          # Carpeta local de persistencia de archivos subidos
├── Dockerfile                       # Multi-stage build para contenedor Docker
├── RESOURCE.md                      # Guía didáctica y conceptual de Spring Boot
├── pom.xml                          # Dependencias y configuración Maven
└── README.md                        # Documentación principal del sistema
```

---

## 📄 Licencia y Autores

* **Desarrollador:** Fermin
* **Proyecto:** Portafolio Personal - API Backend
* **Documentación Adicional:** Consulta [RESOURCE.md](RESOURCE.md) para una guía detallada de aprendizaje sobre todas las anotaciones y patrones de diseño utilizados en este proyecto.


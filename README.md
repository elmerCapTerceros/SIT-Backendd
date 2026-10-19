# SIT Backend

Backend desarrollado en Java con Spring Boot para la gestión de usuarios, roles y solicitudes técnicas dentro del sistema SIT.

## Descripción

Este proyecto expone una API REST para:

- Autenticación y autorización con JWT
- Registro y actualización de usuarios
- Gestión de roles
- Creación y asignación de solicitudes técnicas
- Consulta de solicitudes por usuario y por supervisión

La aplicación usa PostgreSQL como base de datos y Flyway para la gestión de migraciones.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Maven
- Docker Compose

## Requisitos previos

Antes de ejecutar el proyecto asegúrate de tener instalado:

- Java 21
- Maven
- Docker Desktop o Docker Engine
- Git

## Estructura del proyecto

```text
sit-backend/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/gobernacionSIT/sit_backend/
│  │  │     ├─ config/
│  │  │     ├─ controller/
│  │  │     ├─ dto/
│  │  │     ├─ entity/
│  │  │     ├─ exception/
│  │  │     ├─ repository/
│  │  │     ├─ security/
│  │  │     └─ service/
│  │  └─ resources/
│  │     ├─ application.yml
│  │     └─ db/migration/
│  └─ test/
├─ docker-compose.yml
├─ pom.xml
├─ mvnw
├─ mvnw.cmd
├─ .gitignore
└─ README.md
```

## Configuración de base de datos

La aplicación usa PostgreSQL en localhost:5433 por defecto para desarrollo local. Puedes sobrescribir la conexión mediante variables de entorno:

- Base de datos: `sit_db`
- Usuario: `sit_user`
- Contraseña: `sit_pass`

`DB_URL`, `DB_USERNAME` y `DB_PASSWORD` controlan la conexión.

Configura además `JWT_SECRET` con una clave aleatoria de al menos 32 bytes antes de iniciar la aplicación. Por ejemplo, en PowerShell:

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

No reutilices claves de desarrollo en producción.

## Ejecutar con Docker

Desde la raíz del proyecto:

```bash
docker-compose up -d
```

Esto levantará el contenedor PostgreSQL en el puerto `5433`.

## Ejecutar la aplicación

### Opción 1: usando Maven

```bash
./mvn spring-boot:run
```

### Opción 2: usando el wrapper de Windows

```powershell
mvnw.cmd spring-boot:run
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

## Autenticación

La API usa JWT para proteger los endpoints. El flujo principal es:

1. El usuario inicia sesión en `/api/auth/login`
2. El backend valida credenciales
3. Se devuelve un token JWT
4. El cliente envía el token en el header `Authorization: Bearer <token>`

## Endpoints principales

### Autenticación

```http
POST /api/auth/login
```

Ejemplo de body:

```json
{
  "userLogin": "<usuario registrado>",
  "password": "<contraseña>"
}
```

### Usuarios

```http
POST /api/usuarios
GET /api/usuarios/{id}
GET /api/usuarios/tecnicos
GET /api/usuarios/area/{area}
PUT /api/usuarios/me
```

### Solicitudes

```http
POST /api/solicitudes
GET /api/solicitudes/misSolicitudes
PUT /api/solicitudes/{id}/asignacion
GET /api/solicitudes/SolicitudesTecnicas
PATCH /api/solicitudes/{id}/atender
PATCH /api/solicitudes/{id}/rechazar
PATCH /api/solicitudes/{id}/concluir
PATCH /api/solicitudes/{id}/verificar
```

Las acciones de atender, rechazar y concluir son exclusivas del técnico asignado.
El funcionario solicitante puede verificar la recepción solo cuando la solicitud está concluida.
El endpoint de rechazo requiere `{"motivo":"..."}` con un motivo de 10 a 500 caracteres.
Los listados de solicitudes asignadas incluyen el motivo de rechazo y los datos
disponibles del funcionario solicitante.

### Roles

```http
POST /api/roles
GET /api/roles
```

La consulta y creación de roles están limitadas al supervisor; solo se permiten
los roles `FUNCIONARIO`, `TECNICO` y `SUPERVISOR`.

## Roles soportados

El sistema usa roles de seguridad para controlar permisos. En la lógica actual se manejan roles como:

- `FUNCIONARIO`
- `SUPERVISOR`
- `TECNICO` (según el uso del sistema y la lógica de negocio)

## Seguridad

La configuración de seguridad incluye:

- CSRF deshabilitado
- CORS habilitado para `http://localhost:5173`
- Sesiones stateless
- Endpoints públicos en `/api/auth/**` y documentación OpenAPI
- El resto de rutas requiere autenticación
- Validación por `@PreAuthorize` en los controladores
- Las cuentas con `primer_ingreso=true` solo pueden completar `PUT /api/usuarios/me`
  hasta guardar su perfil y una nueva contraseña.

## Migraciones con Flyway

Las migraciones se encuentran en:

```text
src/main/resources/db/migration/
```

Flyway ejecuta automáticamente las migraciones al levantar la aplicación.
La migración V15 elimina de `solicitud` la columna obsoleta `equipo_danado`;
respalda la base de datos antes de desplegar esta versión en otros entornos.
La V16 obliga a completar el perfil inicial del supervisor antes de usar otras rutas.

## Variables y configuración relevantes

Archivo principal:

```yaml
src/main/resources/application.yml
```

Configuraciones principales:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/sit_db
    username: sit_user
    password: sit_pass
app:
  security:
    jwt-secret: ${JWT_SECRET}
```

## Ejemplo de uso rápido

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userLogin":"<usuario registrado>","password":"<contraseña>"}'
```

### Crear una solicitud

```bash
curl -X POST http://localhost:8080/api/solicitudes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "titulo": "Falla de equipo de computo",
    "tipo": "PC",
    "descripcion": "El equipo no prende y presenta pantalla negra"
  }'
```

## Solución de problemas comunes

### Error de conexión a PostgreSQL

Verifica que el contenedor esté levantado:

```bash
docker-compose ps
```

Si no está activo:

```bash
docker-compose up -d
```

### Puerto ocupado

Si el puerto `5433` o `8080` ya está en uso, cambia los valores en:

- `docker-compose.yml`
- `application.yml`

### Error de compilación

```bash
./mvnw clean install
```

## Notas

- Este proyecto está orientado a un entorno interno de la Gobernación.
- Los nombres y roles pueden ajustarse según la estructura final del sistema.
- El README puede ampliarse con documentación de endpoints, ejemplos de front-end y flujos de negocio.

## Autor

Capcito
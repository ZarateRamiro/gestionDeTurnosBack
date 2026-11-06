# Gestión de turnos para canchas

Aplicación para administrar canchas de fútbol, clientes y reservas. El repositorio contiene un backend REST con Spring Boot y un frontend Angular.

## Tecnologías y arquitectura

- Java 25, Spring Boot 4, Spring MVC, Spring Data JPA, Spring Security y MySQL.
- Capas separadas en `controller`, `service`, `service.impl` y `repository`.
- Requests y responses mediante DTOs `record`; validación Jakarta y respuestas de error centralizadas.
- JWT stateless para administradores y clientes, contraseñas BCrypt y autorización por rol.
- Clientes con registro propio, consulta pública de disponibilidad y solicitud autenticada de turnos.
- Angular standalone con rutas lazy, guards por rol, interceptor HTTP, servicios y formularios reactivos.
- Pruebas Java con JUnit 5, Mockito y una prueba de integración sobre H2; pruebas Angular con Vitest.

## Requisitos

- JDK 25.
- MySQL 8 disponible localmente.
- Node.js y npm compatibles con Angular CLI 22.

## Configuración del backend

La base por defecto es `gestion_turnos` en `localhost:3307`, con usuario `root` y contraseña `root` (según `application.properties`). Configurá `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` para usar otros valores. Hibernate crea o actualiza las tablas con `ddl-auto=update`.

En IntelliJ, agregá estas variables en **Run → Edit Configurations… → Environment variables**:

```text
ADMIN_EMAIL=admin@tucancha.com;ADMIN_PASSWORD=una-clave-de-al-menos-12-caracteres;JWT_SECRET=una-clave-aleatoria-de-al-menos-32-caracteres
```

`ADMIN_PASSWORD` debe tener 12 caracteres o más. La cuenta administradora inicial se crea una sola vez y su contraseña se guarda con BCrypt. `JWT_SECRET` debe ser estable entre reinicios; si se omite, la app genera uno temporal y los tokens dejan de servir al reiniciarla. No guardes credenciales reales en el repositorio.

Iniciá `DemoApplication` desde IntelliJ o ejecutá en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Configuración y ejecución del frontend

Iniciá el backend primero. Después, desde `gestionDeTurnosFront`:

```bash
npm install
npm start
```

Abrí `http://localhost:4200`. El proxy de desarrollo reenvía las llamadas `/api` a `http://localhost:8080`. Si cambiás el puerto del backend, actualizá `gestionDeTurnosFront/proxy.conf.json`.

## API

Swagger UI: `http://localhost:8080/swagger-ui.html`.

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Público | Iniciar sesión y recibir un JWT |
| `POST` | `/api/v1/auth/register` | Público | Registrar cuenta de cliente e iniciar sesión |
| `GET` | `/api/v1/canchas` | Público | Listar canchas activas |
| `GET` | `/api/v1/canchas/{id}` | Público | Consultar una cancha |
| `GET` | `/api/v1/disponibilidad?canchaId=1&fecha=2026-12-01&duracionHoras=1` | Público | Consultar horarios libres y precio |
| `POST` | `/api/v1/canchas` | Administrador | Crear una cancha |
| `PUT` | `/api/v1/canchas/{id}` | Administrador | Actualizar una cancha |
| `DELETE` | `/api/v1/canchas/{id}` | Administrador | Desactivar una cancha |
| `GET`, `POST` | `/api/v1/clientes` | Administrador | Listar o crear clientes |
| `GET`, `PUT`, `DELETE` | `/api/v1/clientes/{id}` | Administrador | Consultar, actualizar o eliminar un cliente |
| `POST` | `/api/v1/reservas` | Administrador | Crear una reserva |
| `POST` | `/api/v1/reservas/solicitudes` | Cliente | Solicitar turno para su propia cuenta |
| `GET` | `/api/v1/reservas/mis-reservas` | Cliente | Consultar sus turnos |
| `GET` | `/api/v1/reservas?canchaId=1&fecha=2026-12-01` | Administrador | Consultar turnos de una cancha y fecha |
| `GET` | `/api/v1/reservas/cliente/{usuarioId}` | Administrador | Consultar reservas de un cliente |
| `PATCH` | `/api/v1/reservas/{id}/cancelar` | Administrador | Cancelar una reserva |

Las rutas protegidas requieren `Authorization: Bearer <token>`. Swagger acepta el token en su botón **Authorize**. La API limita CORS al origen `http://localhost:4200` en desarrollo.

## Verificación

Backend:

```powershell
.\mvnw.cmd test
```

Frontend:

```bash
npm test -- --watch=false
npm run build
```

## Planificación para Jira

- [Historias de usuario y criterios de aceptación](docs/historias-de-usuario.md).
- [Estimaciones y propuesta de sprints](docs/plan-de-sprints.md).

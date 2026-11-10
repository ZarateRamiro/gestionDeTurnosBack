# Guía breve para defender el código

Esta guía acompaña los comentarios en el código y resume qué problema resuelve cada anotación importante.

## Backend con Spring Boot

### Capas

- **Controller (`@RestController`)**: recibe HTTP, valida el formato del request y devuelve status/cuerpo. No contiene las reglas principales.
- **DTO (`record`)**: define los datos de entrada/salida de la API. Permite no exponer entidades JPA y validar requests con Jakarta Validation.
- **Service (`@Service`)**: concentra las reglas del negocio. La interfaz declara operaciones y `service.impl` implementa el flujo.
- **Repository (`JpaRepository`)**: delega persistencia a Spring Data. Las consultas `findBy...` se generan por el nombre; `@Query` expresa JPQL cuando la consulta es específica.
- **Model (`@Entity`)**: representa una tabla/relación persistida. `@Column` define restricciones de columnas; `@JoinColumn` define la FK.

### JPA: `EAGER` y `LAZY`

`EAGER` solicita que la relación esté disponible junto con la entidad principal. El proveedor puede resolverla con un join o con consultas adicionales; por eso puede cargar información que ese caso de uso no necesita. No significa literalmente que cada acceso ejecute una nueva consulta ni que siempre bloquee la base.

`LAZY` permite diferir la relación hasta que el código la usa. En `Reserva`, cancha y cliente se cargan al convertir la reserva en `ReservaResponseDTO`; en `UsuarioSistema`, el perfil se consulta para flujos de cliente. Por eso esos métodos del servicio tienen `@Transactional`: la sesión de JPA sigue abierta durante el mapeo. Las respuestas se convierten a DTO dentro del servicio, no se serializan entidades con proxies.

`@ManyToOne` representa muchas reservas relacionadas con una cancha o cliente. `@OneToOne` representa la cuenta de acceso vinculada a un único perfil de cliente. `optional = false` y `nullable = false` expresan que las dos referencias de una reserva son obligatorias.

### Otras anotaciones

- `@Valid` activa las restricciones del DTO (`@NotBlank`, `@Email`, `@Size`, `@FutureOrPresent`, etc.) al recibir un request.
- `@Transactional` agrupa operaciones en una transacción. `readOnly = true` marca consultas sin escritura y mantiene las relaciones LAZY disponibles mientras se construye la respuesta.
- `@RestControllerAdvice` centraliza la conversión de excepciones a errores HTTP.
- `@Enumerated(EnumType.STRING)` almacena el nombre del estado/rol; evita que una reordenación del enum cambie el significado en la base.
- `@Builder`, `@Getter` y similares son de Lombok: generan código repetitivo durante compilación.

### Seguridad

- `SecurityConfig` declara acceso público, acceso autenticado y reglas por rol; también deja la API sin sesión de servidor (`STATELESS`) y define CORS.
- `BCryptPasswordEncoder` guarda un hash de contraseña con salt, no la contraseña original. El factor 12 configura el costo del hash.
- `JwtService` firma y valida el token, incluyendo la firma y expiración. El payload firmado se puede leer: no se deben guardar contraseñas ni secretos dentro del JWT.
- `JwtAuthFilter` procesa `Authorization: Bearer ...` y establece el usuario/rol autenticado para esa petición.
- `@PreAuthorize`/guards del frontend no reemplazan la autorización backend. `SecurityConfig` aplica el permiso definitivo.

## Frontend Angular

- `@Component({standalone: true})` declara una pantalla independiente; `imports` lista lo que necesita su plantilla.
- `@Injectable({providedIn: 'root'})` registra un servicio compartido durante la aplicación.
- `inject(...)` obtiene dependencias del contenedor de Angular, sin construirlas manualmente.
- `signal(...)` contiene estado reactivo; `computed(...)` deriva datos a partir de signals.
- `FormBuilder`, `formGroup` y `Validators` forman formularios reactivos y centralizan reglas visibles para el usuario.
- `HttpClient` realiza llamadas HTTP; `TurnosApiService` concentra endpoints para que las vistas no construyan URLs.
- `HttpInterceptorFn` agrega el JWT y procesa un `401`. `CanActivateFn` (guard) controla navegación en cliente, como ayuda de UX.
- `loadComponent` importa una pantalla al navegar y permite carga diferida.
- `isPlatformBrowser` evita acceder a `localStorage` o llamar a la API durante renderizado SSR.
- `timeout` limita cuánto espera una petición; `finalize` limpia estados de carga tanto en éxito como en error.

## Pruebas, base de datos y Docker

El test `ApiSecurityIntegrationTest` levanta el contexto real de Spring, atraviesa el filtro HTTP con `MockMvc` y configura `jdbc:h2:mem:...`. **No necesita Docker ni MySQL**. H2 se crea en memoria para la prueba y `create-drop` elimina sus tablas al terminar. Los tests de service usan Mockito para simular repositories y no abrir conexión a ninguna base.

Docker/Testcontainers sería útil si se quisiera probar específicamente contra MySQL real, validar diferencias de SQL/migraciones o levantar el entorno completo reproducible. No es requisito para que exista una prueba de integración, y la integración actual ya prueba el login, autorización y endpoints HTTP usando H2.

Backend: ejecutar `./mvnw verify` (Windows: `mvnw.cmd verify`). El reporte JaCoCo queda en `target/site/jacoco/index.html`.

Frontend Angular/Vitest: requiere el paquete `@vitest/coverage-v8` además del runner. Ejecutar `npm test -- --coverage`; el reporte HTML aparece en `coverage/`. En este entorno no se pudo descargar el paquete por falta de conexión a `registry.npmjs.org`; al recuperar internet, instalarlo con:

```bash
npm install --save-dev @vitest/coverage-v8
```

El 80% es un objetivo de cobertura, no una garantía automática de calidad. Conviene medir líneas y ramas, añadir casos para las reglas de negocio y mantener los tests pasando antes de fijar un umbral que haga fallar cada build.

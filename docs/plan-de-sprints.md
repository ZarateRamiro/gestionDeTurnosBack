# Plan inicial de sprints

Propuesta de planificación para cargar en Jira. Duración sugerida: dos semanas por sprint. El equipo puede cambiar duración, puntos y orden según su disponibilidad.

## Sprint 1 — Base y acceso seguro

**Objetivo:** dejar disponible la administración segura, con horarios de atención configurables.

- HU-01 Iniciar sesión como administrador — 5 puntos.
- HU-02 Administrar canchas — 5 puntos.
- HU-03 Registrar y administrar clientes — 5 puntos.
- HU-08 Validar datos y comunicar errores — 3 puntos.

**Total estimado:** 18 puntos.

**Definición de terminado:** endpoints y pantallas integrados; validaciones activas; pruebas unitarias relevantes aprobadas; README actualizado.

## Sprint 2 — Agenda y reservas

**Objetivo:** completar el flujo de consulta, solicitud por clientes, creación y cancelación de turnos.

- HU-04 Consultar turnos ocupados — 3 puntos.
- HU-05 Crear una reserva — 8 puntos.
- HU-06 Consultar reservas de un cliente — 3 puntos.
- HU-07 Cancelar una reserva — 3 puntos.
- HU-09 Registro de cliente y solicitud de turnos — 8 puntos.
- Completar pruebas de integración de los flujos principales — 3 puntos.

**Total estimado:** 28 puntos.

**Definición de terminado:** solapamientos rechazados, precio calculado correctamente, estados visibles en agenda, pruebas backend y frontend aprobadas.

## Ritmo de trabajo en Jira

1. Crear un tablero Scrum y un epic llamado **Gestión de turnos de canchas**.
2. Cargar HU-01 a HU-08 como historias, con criterios, prioridad, estimación y sprint asignado.
3. Crear tareas técnicas debajo de cada historia cuando sea necesario.
4. Vincular cada commit con el identificador del issue, por ejemplo `GT-12 feat: agregar reserva de cancha`.
5. Actualizar el estado del tablero durante el sprint y registrar qué se completó en la revisión.

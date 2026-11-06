# Historias de usuario

Historias preparadas para cargar como issues en Jira. Los puntos son una estimación inicial con escala Fibonacci y se pueden ajustar con el equipo.

## HU-01 — Iniciar sesión según mi perfil

**Como** administrador o cliente, **quiero** iniciar sesión con mi correo y contraseña, **para** acceder a las funciones correspondientes a mi perfil.

**Prioridad:** Alta · **Estimación:** 5 puntos

**Criterios de aceptación:**

- Al ingresar credenciales válidas, recibo un token de acceso con vencimiento.
- Las contraseñas se almacenan con BCrypt y nunca se devuelven por la API.
- Si las credenciales son incorrectas, recibo un error de autenticación sin información sensible.
- Sin token no puedo crear, modificar ni eliminar información protegida.
- Un administrador accede al panel de gestión y un cliente a su portal de turnos.
- Puedo cerrar sesión desde el panel y el cliente deja de enviar el token.

## HU-02 — Administrar canchas

**Como** administrador, **quiero** crear, modificar y desactivar canchas, **para** mantener actualizado el catálogo del complejo.

**Prioridad:** Alta · **Estimación:** 5 puntos

**Criterios de aceptación:**

- Una cancha contiene nombre, dirección, superficie, capacidad, precio por hora y horario de apertura/cierre.
- El horario de cierre debe ser posterior a la apertura y ambos se configuran en horas enteras.
- El administrador puede configurar el horario al crear la cancha y modificarlo después.
- El nombre y la dirección son obligatorios; capacidad y precio deben ser mayores que cero.
- Puedo modificar los datos de una cancha existente.
- Al desactivarla, deja de aparecer en el listado público y se conservan sus reservas previas.
- Las operaciones de administración requieren el rol administrador.

## HU-03 — Registrar y administrar clientes

**Como** administrador, **quiero** registrar clientes con sus datos de contacto, **para** asociarlos a sus reservas.

**Prioridad:** Alta · **Estimación:** 5 puntos

**Criterios de aceptación:**

- El nombre y un correo válido son obligatorios; el teléfono es opcional.
- No puedo registrar dos clientes con el mismo correo.
- Puedo consultar y actualizar los datos de un cliente.
- No puedo eliminar un cliente con reservas asociadas; la API informa el motivo.

## HU-04 — Consultar turnos ocupados

**Como** administrador, **quiero** filtrar la agenda por cancha y fecha, **para** ver los horarios ya reservados.

**Prioridad:** Alta · **Estimación:** 3 puntos

**Criterios de aceptación:**

- La agenda muestra las reservas de la cancha seleccionada para la fecha elegida.
- Cada fila incluye horario, cliente, duración, importe y estado.
- La agenda muestra un estado vacío si no existen reservas y un indicador mientras carga.
- Las reservas canceladas no ocupan el horario.

## HU-05 — Crear una reserva

**Como** administrador, **quiero** reservar una cancha para un cliente en una fecha y horario, **para** registrar un nuevo turno.

**Prioridad:** Alta · **Estimación:** 8 puntos

**Criterios de aceptación:**

- Para guardar, selecciono una cancha, un cliente, una fecha, una hora de inicio y una hora de fin.
- La cancha debe estar activa y el horario debe comenzar en el futuro.
- La hora de fin debe ser posterior a la de inicio y la duración debe ser de horas completas.
- El sistema rechaza horarios superpuestos en la misma cancha y fecha.
- El precio total se calcula usando la duración y el precio por hora de la cancha.
- Al guardar, la reserva queda en estado pendiente y aparece en la agenda.

## HU-06 — Consultar reservas de un cliente

**Como** administrador, **quiero** consultar las reservas asociadas a un cliente, **para** revisar su historial de turnos.

**Prioridad:** Media · **Estimación:** 3 puntos

**Criterios de aceptación:**

- Puedo consultar por identificador de cliente.
- Cada reserva incluye cancha, fecha, horario, importe y estado.
- Si no existe el cliente, la API responde con un error 404 claro.

## HU-07 — Cancelar una reserva

**Como** administrador, **quiero** cancelar una reserva, **para** liberar el horario cuando el cliente no pueda asistir.

**Prioridad:** Alta · **Estimación:** 3 puntos

**Criterios de aceptación:**

- La cancelación cambia el estado a cancelada y no elimina el historial.
- El horario cancelado vuelve a estar disponible para otra reserva.
- Si la reserva no existe o ya está cancelada, el sistema informa el error correspondiente.
- El panel pide confirmación antes de cancelar y muestra el resultado.

## HU-08 — Validar datos y comunicar errores

**Como** usuario del sistema, **quiero** recibir mensajes claros cuando un dato no es válido o una operación falla, **para** corregirla sin perder lo ingresado.

**Prioridad:** Media · **Estimación:** 3 puntos

**Criterios de aceptación:**

- Los formularios señalan los campos obligatorios y formatos inválidos.
- La API valida los requests y devuelve un estado HTTP adecuado.
- El panel muestra mensajes de carga, éxito y error de conexión o validación.
- Los datos inválidos no se guardan.
## HU-09 — Registrarse y reservar un turno

**Como** cliente, **quiero** crear una cuenta, consultar horarios libres y solicitar un turno, **para** reservar una cancha sin depender del administrador.

**Prioridad:** Alta · **Estimación:** 8 puntos

**Criterios de aceptación:**

- El registro solicita nombre, correo, teléfono opcional y contraseña; el correo no puede pertenecer a otra cuenta.
- La contraseña se almacena con BCrypt y la cuenta se crea con rol `CLIENTE`.
- El cliente puede ver canchas activas y consultar horarios según fecha y duración, con el precio calculado.
- Los horarios consultados respetan el horario de atención configurado por el administrador; si no hay otras reservas, se ofrecen todas las franjas dentro de ese horario.
- Solo se ofrecen franjas futuras, dentro del horario de atención y sin superposición con reservas vigentes.
- Al solicitar una franja, el servidor asocia la reserva al cliente autenticado y el estado inicial es `PENDIENTE`.
- El cliente puede consultar sus propios turnos; no puede consultar ni reservar en nombre de otro cliente.

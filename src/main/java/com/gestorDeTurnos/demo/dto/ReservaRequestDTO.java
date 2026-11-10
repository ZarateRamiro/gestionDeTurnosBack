package com.gestorDeTurnos.demo.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

/** Alta administrativa: permite seleccionar al cliente y cancha; campos validados antes del servicio. */
public record ReservaRequestDTO(@NotNull Long canchaId,@NotNull Long usuarioId,@NotNull @FutureOrPresent LocalDate fecha,@NotNull LocalTime horaInicio,@NotNull LocalTime horaFin,@Size(max=500) String observaciones) {}

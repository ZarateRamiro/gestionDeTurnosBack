package com.gestorDeTurnos.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/** El cliente no envía su usuario: la identidad se obtiene del JWT autenticado. */
public record SolicitudReservaRequestDTO(@NotNull Long canchaId, @NotNull LocalDate fecha,
    @NotNull LocalTime horaInicio, @NotNull LocalTime horaFin, @Size(max = 500) String observaciones) {}

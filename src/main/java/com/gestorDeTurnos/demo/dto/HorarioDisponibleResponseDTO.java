package com.gestorDeTurnos.demo.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

/** Intervalo libre mostrado al cliente junto con el precio calculado para esa duración. */
public record HorarioDisponibleResponseDTO(LocalTime horaInicio, LocalTime horaFin, BigDecimal precioTotal) {}

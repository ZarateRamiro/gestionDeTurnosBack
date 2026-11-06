package com.gestorDeTurnos.demo.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record HorarioDisponibleResponseDTO(LocalTime horaInicio, LocalTime horaFin, BigDecimal precioTotal) {}

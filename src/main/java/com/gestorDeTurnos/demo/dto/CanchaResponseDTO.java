package com.gestorDeTurnos.demo.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

/** Vista pública de una cancha; la API devuelve estos datos y no la entidad de persistencia. */
public record CanchaResponseDTO(Long id, String nombre, String direccion, String tipoSuperficie,
    Integer capacidadJugadores, BigDecimal precioPorHora, Boolean activa, LocalTime horaApertura, LocalTime horaCierre) {}

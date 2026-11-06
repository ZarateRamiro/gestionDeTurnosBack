package com.gestorDeTurnos.demo.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CanchaResponseDTO(Long id, String nombre, String direccion, String tipoSuperficie,
    Integer capacidadJugadores, BigDecimal precioPorHora, Boolean activa, LocalTime horaApertura, LocalTime horaCierre) {}

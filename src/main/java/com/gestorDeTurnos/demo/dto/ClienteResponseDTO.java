package com.gestorDeTurnos.demo.dto;

import java.time.LocalDateTime;

/** Datos del cliente que pueden exponerse en respuestas sin incluir información de acceso. */
public record ClienteResponseDTO(Long id, String nombre, String email, String telefono, LocalDateTime fechaAlta) {}

package com.gestorDeTurnos.demo.dto;

import java.time.LocalDateTime;

public record ClienteResponseDTO(Long id, String nombre, String email, String telefono, LocalDateTime fechaAlta) {}

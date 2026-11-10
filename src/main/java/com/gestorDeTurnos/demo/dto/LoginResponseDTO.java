package com.gestorDeTurnos.demo.dto;

/** Resultado del login: JWT, tipo/expiración y datos mínimos para adaptar la interfaz al rol. */
public record LoginResponseDTO(String token, String tipo, long expiraEnSegundos, String nombre, String rol, Long clienteId) {}

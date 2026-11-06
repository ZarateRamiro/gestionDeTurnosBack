package com.gestorDeTurnos.demo.dto;

public record LoginResponseDTO(String token, String tipo, long expiraEnSegundos, String nombre, String rol, Long clienteId) {}

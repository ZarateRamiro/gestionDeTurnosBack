package com.gestorDeTurnos.demo.dto;
import jakarta.validation.constraints.*;
public record UsuarioRequestDTO(
    @NotBlank @Size(max=100) String nombre,
    @NotBlank @Email @Size(max=150) String email,@Size(max=30) String telefono) {}

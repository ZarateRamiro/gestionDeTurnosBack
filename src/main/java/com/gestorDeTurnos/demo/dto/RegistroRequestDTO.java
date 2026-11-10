package com.gestorDeTurnos.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de alta de cliente; el rol se asigna en el servidor para que el registro no otorgue privilegios. */
public record RegistroRequestDTO(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String telefono,
    @NotBlank @Size(min = 12, max = 72) String password) {}

package com.gestorDeTurnos.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Credenciales de entrada; las restricciones se ejecutan al usar @Valid en el controller. */
public record LoginRequestDTO(@NotBlank @Email @Size(max = 150) String email, @NotBlank @Size(max = 100) String password) {}

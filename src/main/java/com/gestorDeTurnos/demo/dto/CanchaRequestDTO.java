package com.gestorDeTurnos.demo.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;
public record CanchaRequestDTO(@NotBlank @Size(max=100) String nombre,@NotBlank @Size(max=150) String direccion,@Size(max=30) String tipoSuperficie,@NotNull @Min(2) @Max(30) Integer capacidadJugadores,@NotNull @DecimalMin("0.01") BigDecimal precioPorHora,LocalTime horaApertura,LocalTime horaCierre) {
  public CanchaRequestDTO(String nombre,String direccion,String tipoSuperficie,Integer capacidadJugadores,BigDecimal precioPorHora) {
    this(nombre,direccion,tipoSuperficie,capacidadJugadores,precioPorHora,null,null);
  }
}

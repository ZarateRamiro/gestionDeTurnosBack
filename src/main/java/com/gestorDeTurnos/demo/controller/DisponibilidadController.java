package com.gestorDeTurnos.demo.controller;

import com.gestorDeTurnos.demo.dto.HorarioDisponibleResponseDTO;
import com.gestorDeTurnos.demo.service.DisponibilidadService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Expone la consulta pública de franjas y delega el cálculo al servicio. */
@RestController
@RequestMapping("/api/v1/disponibilidad")
public class DisponibilidadController {
  private final DisponibilidadService service;

  public DisponibilidadController(DisponibilidadService service) {
    this.service = service;
  }

  @GetMapping
  public List<HorarioDisponibleResponseDTO> consultar(@RequestParam Long canchaId,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                      @RequestParam(defaultValue = "1") int duracionHoras) {
    return service.consultar(canchaId, fecha, duracionHoras);
  }
}

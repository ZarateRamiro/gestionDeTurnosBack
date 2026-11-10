package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.HorarioDisponibleResponseDTO;
import java.time.LocalDate;
import java.util.List;

/** Contrato para calcular los intervalos libres de una cancha en una fecha. */
public interface DisponibilidadService {
  List<HorarioDisponibleResponseDTO> consultar(Long canchaId, LocalDate fecha, int duracionHoras);
}

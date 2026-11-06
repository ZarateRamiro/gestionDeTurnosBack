package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.HorarioDisponibleResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadService {
  List<HorarioDisponibleResponseDTO> consultar(Long canchaId, LocalDate fecha, int duracionHoras);
}

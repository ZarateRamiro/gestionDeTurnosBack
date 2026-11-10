package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.CanchaRequestDTO;
import com.gestorDeTurnos.demo.dto.CanchaResponseDTO;
import java.util.List;

/** Operaciones de negocio disponibles para administrar canchas. */
public interface CanchaService {
  List<CanchaResponseDTO> listar();
  CanchaResponseDTO obtener(Long id);
  CanchaResponseDTO crear(CanchaRequestDTO request);
  CanchaResponseDTO actualizar(Long id, CanchaRequestDTO request);
  void desactivar(Long id);
}

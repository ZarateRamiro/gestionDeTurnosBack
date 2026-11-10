package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.ReservaRequestDTO;
import com.gestorDeTurnos.demo.dto.ReservaResponseDTO;
import com.gestorDeTurnos.demo.dto.SolicitudReservaRequestDTO;
import java.time.LocalDate;
import java.util.List;

/** Reglas de aplicación para solicitar, consultar y cancelar reservas. */
public interface ReservaService {
  ReservaResponseDTO crear(ReservaRequestDTO request);
  ReservaResponseDTO solicitar(String email, SolicitudReservaRequestDTO request);
  List<ReservaResponseDTO> misReservas(String email);
  List<ReservaResponseDTO> porCanchaFecha(Long canchaId, LocalDate fecha);
  List<ReservaResponseDTO> porUsuario(Long usuarioId);
  ReservaResponseDTO cancelar(Long id);
}

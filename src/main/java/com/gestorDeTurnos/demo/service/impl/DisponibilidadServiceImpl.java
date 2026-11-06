package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.HorarioDisponibleResponseDTO;
import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.service.DisponibilidadService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {
  private final CanchaRepository canchas;
  private final ReservaRepository reservas;
  public DisponibilidadServiceImpl(CanchaRepository canchas, ReservaRepository reservas) { this.canchas = canchas; this.reservas = reservas; }

  @Override public List<HorarioDisponibleResponseDTO> consultar(Long canchaId, LocalDate fecha, int duracionHoras) {
    if (duracionHoras < 1 || duracionHoras > 3) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración debe ser de 1 a 3 horas");
    if (fecha.isBefore(LocalDate.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha debe ser hoy o posterior");
    Cancha cancha = canchas.findById(canchaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
    if (!Boolean.TRUE.equals(cancha.getActiva())) throw new ResponseStatusException(HttpStatus.CONFLICT, "La cancha no está disponible");
    LocalTime apertura = cancha.getHoraApertura() == null ? LocalTime.of(8, 0) : cancha.getHoraApertura();
    LocalTime cierre = cancha.getHoraCierre() == null ? LocalTime.of(23, 0) : cancha.getHoraCierre();
    List<HorarioDisponibleResponseDTO> disponibles = new ArrayList<>();
    for (LocalTime inicio = apertura; !inicio.plusHours(duracionHoras).isAfter(cierre); inicio = inicio.plusHours(1)) {
      LocalTime fin = inicio.plusHours(duracionHoras);
      if (fecha.equals(LocalDate.now()) && !inicio.isAfter(LocalTime.now())) continue;
      if (!reservas.existeSuperposicion(canchaId, fecha, inicio, fin, EstadoReserva.CANCELADA))
        disponibles.add(new HorarioDisponibleResponseDTO(inicio, fin, cancha.getPrecioPorHora().multiply(java.math.BigDecimal.valueOf(duracionHoras))));
    }
    return disponibles;
  }
}

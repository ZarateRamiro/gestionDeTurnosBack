package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.HorarioDisponibleResponseDTO;
import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.model.Reserva;
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

/** Calcula franjas de una hora que caben en el horario de la cancha y no se superponen con reservas. */
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
    // Se consulta una sola vez el día completo; cada bloque candidato se compara en memoria.
    List<Reserva> reservasDelDia = reservas.findByCanchaIdAndFechaAndEstadoNotOrderByHoraInicio(canchaId, fecha, EstadoReserva.CANCELADA);
    LocalTime ultimoInicio = cierre.minusHours(duracionHoras);
    LocalTime horaActual = LocalTime.now();
    for (LocalTime inicio = apertura; !inicio.isAfter(ultimoInicio); inicio = inicio.plusHours(1)) {
      LocalTime fin = inicio.plusHours(duracionHoras);
      LocalTime horaInicio = inicio;
      // Para hoy, oculta los bloques que ya comenzaron aunque todavía no hayan terminado.
      if (fecha.equals(LocalDate.now()) && !inicio.isAfter(horaActual)) continue;
      // Dos intervalos chocan cuando uno empieza antes de que termine el otro y viceversa.
      boolean ocupada = reservasDelDia.stream()
          .anyMatch(reserva -> reserva.getHoraInicio().isBefore(fin) && reserva.getHoraFin().isAfter(horaInicio));
      if (!ocupada)
        disponibles.add(new HorarioDisponibleResponseDTO(inicio, fin, cancha.getPrecioPorHora().multiply(java.math.BigDecimal.valueOf(duracionHoras))));
    }
    return disponibles;
  }
}

package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.ReservaRequestDTO;
import com.gestorDeTurnos.demo.dto.ReservaResponseDTO;
import com.gestorDeTurnos.demo.dto.SolicitudReservaRequestDTO;
import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.model.Reserva;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import com.gestorDeTurnos.demo.model.UsuarioSistema;
import com.gestorDeTurnos.demo.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {
  private final ReservaRepository reservas;
  private final CanchaRepository canchas;
  private final UsuarioRepository usuarios;
  private final UsuarioSistemaRepository cuentas;

  public ReservaServiceImpl(ReservaRepository reservas, CanchaRepository canchas, UsuarioRepository usuarios, UsuarioSistemaRepository cuentas) {
    this.reservas = reservas; this.canchas = canchas; this.usuarios = usuarios; this.cuentas = cuentas;
  }

  // Mantiene abiertas las relaciones LAZY mientras valida y convierte a DTO.
  @Override @Transactional
  public ReservaResponseDTO solicitar(String email, SolicitudReservaRequestDTO request) {
    UsuarioSistema cuenta = cuentas.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (cuenta.getCliente() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta no tiene un perfil de cliente");
    long duracionMinutos = Duration.between(request.horaInicio(), request.horaFin()).toMinutes();
    if (request.horaInicio().getMinute() != 0 || request.horaInicio().getSecond() != 0 || duracionMinutos < 60 || duracionMinutos > 180 || duracionMinutos % 60 != 0)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elegí una franja de 1 a 3 horas comenzando en una hora en punto");
    return crear(new ReservaRequestDTO(request.canchaId(), cuenta.getCliente().getId(), request.fecha(), request.horaInicio(), request.horaFin(), request.observaciones()));
  }

  // El DTO usa cancha y cliente: la carga LAZY ocurre bajo una transacción de solo lectura.
  @Override @Transactional(readOnly = true) public List<ReservaResponseDTO> misReservas(String email) {
    UsuarioSistema cuenta = cuentas.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (cuenta.getCliente() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta no tiene un perfil de cliente");
    return porUsuario(cuenta.getCliente().getId());
  }

  // La escritura y el mapeo de relaciones LAZY se completan en la misma transacción.
  @Override @Transactional
  public ReservaResponseDTO crear(ReservaRequestDTO r) {
    if (!r.horaFin().isAfter(r.horaInicio())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de fin debe ser posterior a la de inicio");
    if (r.fecha().isBefore(LocalDate.now()) || (r.fecha().equals(LocalDate.now()) && !r.horaInicio().isAfter(LocalTime.now()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El turno debe comenzar en el futuro");
    Cancha c = canchas.findById(r.canchaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
    if (!Boolean.TRUE.equals(c.getActiva())) throw new ResponseStatusException(HttpStatus.CONFLICT, "La cancha no está disponible");
    LocalTime apertura = c.getHoraApertura() == null ? LocalTime.of(8, 0) : c.getHoraApertura();
    LocalTime cierre = c.getHoraCierre() == null ? LocalTime.of(23, 0) : c.getHoraCierre();
    if (r.horaInicio().isBefore(apertura) || r.horaFin().isAfter(cierre)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El turno debe estar dentro del horario de atención de la cancha");
    Usuario u = usuarios.findById(r.usuarioId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    if (reservas.existeSuperposicion(c.getId(), r.fecha(), r.horaInicio(), r.horaFin(), EstadoReserva.CANCELADA)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese horario ya está reservado");
    long minutos = Duration.between(r.horaInicio(), r.horaFin()).toMinutes();
    if (minutos % 60 != 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración debe ser múltiplo de una hora");
    Reserva reserva = reservas.save(Reserva.builder().cancha(c).usuario(u).fecha(r.fecha()).horaInicio(r.horaInicio()).horaFin(r.horaFin()).precioTotal(c.getPrecioPorHora().multiply(BigDecimal.valueOf(minutos / 60))).observaciones(r.observaciones()).build());
    return map(reserva);
  }

  // map() accede a relaciones LAZY mientras arma cada respuesta.
  @Override @Transactional(readOnly = true) public List<ReservaResponseDTO> porCanchaFecha(Long canchaId, LocalDate fecha) {
    if (!canchas.existsById(canchaId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada");
    return reservas.findByCanchaIdAndFechaAndEstadoNotOrderByHoraInicio(canchaId, fecha, EstadoReserva.CANCELADA).stream().map(this::map).toList();
  }
  // Consulta de solo lectura; la transacción permite mapear las relaciones LAZY.
  @Override @Transactional(readOnly = true) public List<ReservaResponseDTO> porUsuario(Long id) {
    if (!usuarios.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
    return reservas.findByUsuarioIdOrderByFechaDescHoraInicioDesc(id).stream().map(this::map).toList();
  }
  @Override @Transactional public ReservaResponseDTO cancelar(Long id) {
    Reserva r = reservas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    if (r.getEstado() == EstadoReserva.CANCELADA) throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva ya está cancelada");
    r.setEstado(EstadoReserva.CANCELADA); return map(r);
  }
  private ReservaResponseDTO map(Reserva r) {
    return new ReservaResponseDTO(r.getId(), r.getCancha().getId(), r.getCancha().getNombre(), r.getUsuario().getId(), r.getUsuario().getNombre(), r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getEstado(), r.getPrecioTotal(), r.getObservaciones(), r.getCreadaEn());
  }
}

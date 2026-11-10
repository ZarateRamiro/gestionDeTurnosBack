package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.CanchaRequestDTO;
import com.gestorDeTurnos.demo.dto.CanchaResponseDTO;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.service.CanchaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.time.LocalTime;

/** Implementa reglas de cancha y traduce entidades JPA a DTOs de respuesta. */
@Service
public class CanchaServiceImpl implements CanchaService {
  private final CanchaRepository repo;
  public CanchaServiceImpl(CanchaRepository repo) { this.repo = repo; }
  @Override public List<CanchaResponseDTO> listar() { return repo.findByActivaTrue().stream().map(this::toResponse).toList(); }
  @Override public CanchaResponseDTO obtener(Long id) { return toResponse(findEntity(id)); }
  @Override public CanchaResponseDTO crear(CanchaRequestDTO r) {
    // Si el administrador omite el rango horario, se usa la franja estándar del negocio.
    LocalTime apertura = r.horaApertura() == null ? LocalTime.of(8, 0) : r.horaApertura();
    LocalTime cierre = r.horaCierre() == null ? LocalTime.of(23, 0) : r.horaCierre();
    validarHorario(apertura, cierre);
    return toResponse(repo.save(Cancha.builder().nombre(r.nombre()).direccion(r.direccion()).tipoSuperficie(r.tipoSuperficie())
        .capacidadJugadores(r.capacidadJugadores()).precioPorHora(r.precioPorHora())
        .horaApertura(apertura).horaCierre(cierre).build()));
  }
  @Override public CanchaResponseDTO actualizar(Long id, CanchaRequestDTO r) {
    Cancha c = findEntity(id);
    LocalTime opening = r.horaApertura() == null ? horarioApertura(c) : r.horaApertura();
    LocalTime closing = r.horaCierre() == null ? horarioCierre(c) : r.horaCierre();
    validarHorario(opening, closing);
    c.setNombre(r.nombre()); c.setDireccion(r.direccion()); c.setTipoSuperficie(r.tipoSuperficie()); c.setCapacidadJugadores(r.capacidadJugadores()); c.setPrecioPorHora(r.precioPorHora()); c.setHoraApertura(opening); c.setHoraCierre(closing); return toResponse(repo.save(c));
  }
  @Override public void desactivar(Long id) { Cancha c = findEntity(id); c.setActiva(false); repo.save(c); }
  private Cancha findEntity(Long id) { return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada")); }
  private void validarHorario(LocalTime apertura, LocalTime cierre) {
    // Se exigen horas enteras porque la disponibilidad se ofrece en bloques de horas completas.
    if (apertura != null && cierre != null && !cierre.isAfter(apertura)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El horario de cierre debe ser posterior a la apertura");
    if ((apertura != null && apertura.getMinute() != 0) || (cierre != null && cierre.getMinute() != 0)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El horario de atención debe configurarse en horas enteras");
  }
  private LocalTime horarioApertura(Cancha c) { return c.getHoraApertura() == null ? LocalTime.of(8, 0) : c.getHoraApertura(); }
  private LocalTime horarioCierre(Cancha c) { return c.getHoraCierre() == null ? LocalTime.of(23, 0) : c.getHoraCierre(); }
  private CanchaResponseDTO toResponse(Cancha c) { return new CanchaResponseDTO(c.getId(), c.getNombre(), c.getDireccion(), c.getTipoSuperficie(), c.getCapacidadJugadores(), c.getPrecioPorHora(), c.getActiva(), horarioApertura(c), horarioCierre(c)); }
}

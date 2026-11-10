package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.model.Reserva;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.model.UsuarioSistema;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import com.gestorDeTurnos.demo.dto.ReservaRequestDTO;
import com.gestorDeTurnos.demo.dto.SolicitudReservaRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {
  @Mock private ReservaRepository reservas;
  @Mock private CanchaRepository canchas;
  @Mock private UsuarioRepository usuarios;
  @Mock private UsuarioSistemaRepository cuentas;
  @InjectMocks private ReservaServiceImpl service;

  @Test void rechazaUnaReservaQueSeSuperponeConOtra() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    when(canchas.findById(1L)).thenReturn(Optional.of(Cancha.builder().id(1L).nombre("Cancha 1").activa(true).precioPorHora(new BigDecimal("30000")).build()));
    when(usuarios.findById(2L)).thenReturn(Optional.of(com.gestorDeTurnos.demo.model.Usuario.builder().id(2L).nombre("Ana").build()));
    when(reservas.existeSuperposicion(1L, fecha, LocalTime.of(18, 0), LocalTime.of(19, 0), EstadoReserva.CANCELADA)).thenReturn(true);
    var request = new ReservaRequestDTO(1L, 2L, fecha, LocalTime.of(18, 0), LocalTime.of(19, 0), null);

    var error = assertThrows(ResponseStatusException.class, () -> service.crear(request));

    assertEquals(409, error.getStatusCode().value());
    verify(reservas, never()).save(any());
  }

  @Test void creaReservaYCalculaPrecioPorDuracion() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    Cancha cancha = Cancha.builder().id(1L).nombre("Cancha 1").activa(true)
        .precioPorHora(new BigDecimal("30000")).horaApertura(LocalTime.of(8, 0)).horaCierre(LocalTime.of(23, 0)).build();
    Usuario usuario = Usuario.builder().id(2L).nombre("Ana").build();
    when(canchas.findById(1L)).thenReturn(Optional.of(cancha));
    when(usuarios.findById(2L)).thenReturn(Optional.of(usuario));
    when(reservas.existeSuperposicion(anyLong(), any(), any(), any(), any())).thenReturn(false);
    when(reservas.save(any(Reserva.class))).thenAnswer(invocation -> {
      Reserva booking = invocation.getArgument(0); booking.setId(6L); return booking;
    });
    var request = new ReservaRequestDTO(1L, 2L, fecha, LocalTime.of(18, 0), LocalTime.of(20, 0), null);
    var response = service.crear(request);
    assertEquals(6L, response.id());
    assertEquals(new BigDecimal("60000"), response.precioTotal());
    assertEquals("Ana", response.cliente());
  }

  @Test void rechazaUnaReservaConHoraDeFinAnteriorAlInicio() {
    var request = new ReservaRequestDTO(1L, 2L, LocalDate.now().plusDays(1), LocalTime.of(20, 0), LocalTime.of(19, 0), null);
    assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.crear(request)).getStatusCode().value());
    verifyNoInteractions(canchas, usuarios);
  }

  @Test void rechazaUnaReservaQueEmpiezaEnElPasado() {
    var request = new ReservaRequestDTO(1L, 2L, LocalDate.now().minusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), null);
    assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.crear(request)).getStatusCode().value());
  }

  @Test void cancelaSinBorrarLaReserva() {
    Reserva booking = Reserva.builder().id(4L).cancha(Cancha.builder().id(1L).nombre("Cancha").build())
        .usuario(com.gestorDeTurnos.demo.model.Usuario.builder().id(2L).nombre("Ana").build())
        .estado(EstadoReserva.PENDIENTE).build();
    when(reservas.findById(4L)).thenReturn(Optional.of(booking));
    assertEquals(EstadoReserva.CANCELADA, service.cancelar(4L).estado());
    verify(reservas, never()).delete(any());
  }

  @Test void noCancelaDosVecesLaMismaReserva() {
    when(reservas.findById(4L)).thenReturn(Optional.of(Reserva.builder().id(4L).estado(EstadoReserva.CANCELADA).build()));
    assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.cancelar(4L)).getStatusCode().value());
  }

  @Test void clienteConsultaSoloLasReservasAsociadasASuPerfil() {
    Usuario cliente = Usuario.builder().id(2L).nombre("Ana").build();
    UsuarioSistema cuenta = UsuarioSistema.builder().email("ana@test.local").cliente(cliente).build();
    Reserva booking = Reserva.builder().id(5L).cancha(Cancha.builder().id(1L).nombre("Cancha 1").build())
        .usuario(cliente).fecha(LocalDate.now().plusDays(1)).horaInicio(LocalTime.of(18, 0)).horaFin(LocalTime.of(19, 0))
        .estado(EstadoReserva.PENDIENTE).precioTotal(new BigDecimal("30000")).build();
    when(cuentas.findByEmailIgnoreCase("ana@test.local")).thenReturn(Optional.of(cuenta));
    when(usuarios.existsById(2L)).thenReturn(true);
    when(reservas.findByUsuarioIdOrderByFechaDescHoraInicioDesc(2L)).thenReturn(List.of(booking));
    var result = service.misReservas("ana@test.local");
    assertEquals(1, result.size());
    assertEquals("Cancha 1", result.getFirst().cancha());
  }
}

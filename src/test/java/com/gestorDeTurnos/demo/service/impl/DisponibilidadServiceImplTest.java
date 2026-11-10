package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.model.Reserva;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/** Prueba la generación de franjas sin conectarse a una base de datos. */
@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceImplTest {
  @Mock private CanchaRepository canchas;
  @Mock private ReservaRepository reservas;
  @InjectMocks private DisponibilidadServiceImpl service;

  private final LocalDate fechaFutura = LocalDate.now().plusDays(2);
  private final Cancha cancha = Cancha.builder()
      .id(1L).activa(true).horaApertura(LocalTime.of(8, 0)).horaCierre(LocalTime.of(23, 0))
      .precioPorHora(new BigDecimal("35000")).build();

  @BeforeEach
  void prepararCancha() { lenient().when(canchas.findById(1L)).thenReturn(Optional.of(cancha)); }

  @Test
  void devuelveFranjasLibresYNoDaLaVueltaAlLlegarAlCierre() {
    Reserva ocupada = Reserva.builder().horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(11, 0)).build();
    when(reservas.findByCanchaIdAndFechaAndEstadoNotOrderByHoraInicio(1L, fechaFutura, EstadoReserva.CANCELADA))
        .thenReturn(List.of(ocupada));

    var horarios = service.consultar(1L, fechaFutura, 1);

    assertEquals(14, horarios.size());
    assertFalse(horarios.stream().anyMatch(h -> h.horaInicio().equals(LocalTime.of(10, 0))));
    assertTrue(horarios.stream().anyMatch(h -> h.horaInicio().equals(LocalTime.of(22, 0)) && h.horaFin().equals(LocalTime.of(23, 0))));
    assertTrue(horarios.stream().allMatch(h -> h.precioTotal().compareTo(new BigDecimal("35000")) == 0));
  }

  @Test
  void devuelveTodasLasFranjasCuandoNoHayReservas() {
    when(reservas.findByCanchaIdAndFechaAndEstadoNotOrderByHoraInicio(1L, fechaFutura, EstadoReserva.CANCELADA))
        .thenReturn(List.of());
    var horarios = service.consultar(1L, fechaFutura, 1);
    assertEquals(15, horarios.size());
    assertEquals(LocalTime.of(8, 0), horarios.getFirst().horaInicio());
    assertEquals(LocalTime.of(22, 0), horarios.getLast().horaInicio());
  }

  @Test
  void rechazaDuracionesFueraDelRangoPermitido() {
    assertThrows(ResponseStatusException.class, () -> service.consultar(1L, fechaFutura, 4));
  }

  @Test
  void rechazaUnaCanchaInactiva() {
    cancha.setActiva(false);
    assertThrows(ResponseStatusException.class, () -> service.consultar(1L, fechaFutura, 1));
  }
}

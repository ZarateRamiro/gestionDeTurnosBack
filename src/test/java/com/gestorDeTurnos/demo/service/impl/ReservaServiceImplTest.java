package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.dto.ReservaRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {
  @Mock private ReservaRepository reservas;
  @Mock private CanchaRepository canchas;
  @Mock private UsuarioRepository usuarios;
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
}

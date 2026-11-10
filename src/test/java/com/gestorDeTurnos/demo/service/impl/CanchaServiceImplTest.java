package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.CanchaRequestDTO;
import com.gestorDeTurnos.demo.model.Cancha;
import com.gestorDeTurnos.demo.repository.CanchaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CanchaServiceImplTest {
  @Mock private CanchaRepository repository;
  @InjectMocks private CanchaServiceImpl service;

  @Test void crearDevuelveDtoConIdYPrecioDeLaCanchaGuardada() {
    var request = new CanchaRequestDTO("Cancha Norte", "Av. Rivadavia 100", "Sintético", 10, new BigDecimal("35000"));
    when(repository.save(any(Cancha.class))).thenAnswer(invocation -> {
      Cancha cancha = invocation.getArgument(0); cancha.setId(7L); return cancha;
    });

    var response = service.crear(request);

    assertEquals(7L, response.id());
    assertEquals("Cancha Norte", response.nombre());
    assertEquals(new BigDecimal("35000"), response.precioPorHora());
  }

  @Test void obtenerCanchaInexistenteDevuelve404() {
    when(repository.findById(42L)).thenReturn(Optional.empty());
    assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.obtener(42L)).getStatusCode().value());
  }

  @Test void crearUsaHorarioPredeterminadoSiNoSeIndica() {
    var request = new CanchaRequestDTO("Cancha Sur", "Centro", "Sintético", 10, new BigDecimal("20000"));
    when(repository.save(any(Cancha.class))).thenAnswer(invocation -> invocation.getArgument(0));
    var response = service.crear(request);
    assertEquals(LocalTime.of(8, 0), response.horaApertura());
    assertEquals(LocalTime.of(23, 0), response.horaCierre());
  }

  @Test void crearRechazaHorarioQueNoEsDeHoraEntera() {
    var request = new CanchaRequestDTO("Cancha Sur", "Centro", "Sintético", 10,
        new BigDecimal("20000"), LocalTime.of(8, 30), LocalTime.of(23, 0));
    assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.crear(request)).getStatusCode().value());
    verify(repository, never()).save(any());
  }

  @Test void listarDevuelveSoloElCatalogoActivoQueEntregaElRepositorio() {
    Cancha cancha = Cancha.builder().id(2L).nombre("Cancha Sur").activa(true)
        .precioPorHora(new BigDecimal("20000")).build();
    when(repository.findByActivaTrue()).thenReturn(List.of(cancha));
    assertEquals("Cancha Sur", service.listar().getFirst().nombre());
  }

  @Test void actualizarGuardaLosNuevosDatosYHorario() {
    Cancha cancha = Cancha.builder().id(2L).nombre("Vieja").activa(true)
        .horaApertura(LocalTime.of(8, 0)).horaCierre(LocalTime.of(22, 0)).build();
    when(repository.findById(2L)).thenReturn(Optional.of(cancha));
    when(repository.save(any(Cancha.class))).thenAnswer(invocation -> invocation.getArgument(0));
    var request = new CanchaRequestDTO("Renovada", "Nueva dirección", "Pasto", 12,
        new BigDecimal("25000"), LocalTime.of(9, 0), LocalTime.of(23, 0));
    var response = service.actualizar(2L, request);
    assertEquals("Renovada", response.nombre());
    assertEquals(LocalTime.of(9, 0), response.horaApertura());
  }

  @Test void desactivarConservaLaCanchaPeroLaMarcaInactiva() {
    Cancha cancha = Cancha.builder().id(2L).activa(true).build();
    when(repository.findById(2L)).thenReturn(Optional.of(cancha));
    when(repository.save(any(Cancha.class))).thenAnswer(invocation -> invocation.getArgument(0));
    service.desactivar(2L);
    assertFalse(cancha.getActiva());
  }
}

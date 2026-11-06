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
}

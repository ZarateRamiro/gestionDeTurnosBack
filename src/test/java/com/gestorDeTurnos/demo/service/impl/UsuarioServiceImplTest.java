package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.UsuarioRequestDTO;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Prueba las reglas del perfil de cliente sin levantar Spring ni una base de datos. */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {
  @Mock private UsuarioRepository usuarios;
  @Mock private ReservaRepository reservas;
  @InjectMocks private UsuarioServiceImpl service;

  @Test
  void creaClienteYNormalizaElCorreo() {
    when(usuarios.existsByEmailIgnoreCase(" ANA@Test.local ")).thenReturn(false);
    when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
      Usuario user = invocation.getArgument(0); user.setId(5L); return user;
    });
    var response = service.crear(new UsuarioRequestDTO("Ana", " ANA@Test.local ", "123"));
    assertEquals(5L, response.id());
    assertEquals("ana@test.local", response.email());
  }

  @Test
  void noPermiteCrearCorreoDuplicado() {
    when(usuarios.existsByEmailIgnoreCase("ana@test.local")).thenReturn(true);
    assertEquals(409, assertThrows(ResponseStatusException.class,
        () -> service.crear(new UsuarioRequestDTO("Ana", "ana@test.local", null))).getStatusCode().value());
    verify(usuarios, never()).save(any());
  }

  @Test
  void listaClientes() {
    when(usuarios.findAll()).thenReturn(List.of(Usuario.builder().id(1L).nombre("Ana").email("ana@test.local").build()));
    assertEquals(1, service.listar().size());
  }

  @Test
  void actualizaLosDatosDelCliente() {
    Usuario user = Usuario.builder().id(1L).nombre("Nombre viejo").email("viejo@test.local").build();
    when(usuarios.findById(1L)).thenReturn(Optional.of(user));
    when(usuarios.existsByEmailIgnoreCaseAndIdNot(" NUEVO@test.local ", 1L)).thenReturn(false);
    when(usuarios.save(user)).thenReturn(user);
    var response = service.actualizar(1L, new UsuarioRequestDTO("Ana", " NUEVO@test.local ", "555"));
    assertEquals("Ana", response.nombre());
    assertEquals("nuevo@test.local", response.email());
  }

  @Test
  void rechazaEliminarUnClienteConReservas() {
    when(usuarios.findById(1L)).thenReturn(Optional.of(Usuario.builder().id(1L).build()));
    when(reservas.existsByUsuarioId(1L)).thenReturn(true);
    assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.eliminar(1L)).getStatusCode().value());
    verify(usuarios, never()).delete(any(Usuario.class));
  }

  @Test
  void eliminaUnClienteSinReservas() {
    Usuario user = Usuario.builder().id(1L).build();
    when(usuarios.findById(1L)).thenReturn(Optional.of(user));
    when(reservas.existsByUsuarioId(1L)).thenReturn(false);
    service.eliminar(1L);
    verify(usuarios).delete(user);
  }
}

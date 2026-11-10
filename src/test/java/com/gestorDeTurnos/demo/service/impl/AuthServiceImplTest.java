package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.RegistroRequestDTO;
import com.gestorDeTurnos.demo.enums.RolSistema;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.model.UsuarioSistema;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import com.gestorDeTurnos.demo.security.jwt.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Prueba el alta segura de cuentas cliente aislando proveedores y repositorios. */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Mock private AuthenticationManager authenticationManager;
  @Mock private UsuarioSistemaRepository cuentas;
  @Mock private JwtService jwt;
  @Mock private UsuarioRepository clientes;
  @Mock private PasswordEncoder encoder;
  @InjectMocks private AuthServiceImpl service;

  @Test
  void registraClienteGuardaHashYDevuelveTokenConRolCliente() {
    when(cuentas.existsByEmailIgnoreCase("ana@test.local")).thenReturn(false);
    when(clientes.findByEmailIgnoreCase("ana@test.local")).thenReturn(Optional.empty());
    when(clientes.save(any(Usuario.class))).thenAnswer(invocation -> {
      Usuario client = invocation.getArgument(0); client.setId(9L); return client;
    });
    when(encoder.encode("ClaveSegura123")).thenReturn("hash-bcrypt");
    when(cuentas.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(jwt.generateToken(any(UsuarioSistema.class))).thenReturn("jwt-test");
    when(jwt.getExpirationSeconds()).thenReturn(28800L);

    var response = service.registrar(new RegistroRequestDTO(" Ana ", " ANA@Test.local ", "555", "ClaveSegura123"));

    assertEquals("jwt-test", response.token());
    assertEquals("CLIENTE", response.rol());
    assertEquals(9L, response.clienteId());
    verify(encoder).encode("ClaveSegura123");
    verify(cuentas).save(argThat(account -> account.getPassword().equals("hash-bcrypt") && account.getRol() == RolSistema.CLIENTE));
  }

  @Test
  void rechazaRegistrarUnCorreoQueYaTieneCuenta() {
    when(cuentas.existsByEmailIgnoreCase("ana@test.local")).thenReturn(true);
    var error = assertThrows(ResponseStatusException.class,
        () -> service.registrar(new RegistroRequestDTO("Ana", "ana@test.local", null, "ClaveSegura123")));
    assertEquals(409, error.getStatusCode().value());
    verify(cuentas, never()).save(any());
  }
}

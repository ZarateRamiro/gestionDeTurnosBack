package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.LoginRequestDTO;
import com.gestorDeTurnos.demo.dto.LoginResponseDTO;
import com.gestorDeTurnos.demo.dto.RegistroRequestDTO;
import com.gestorDeTurnos.demo.enums.RolSistema;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.model.UsuarioSistema;
import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.security.jwt.JwtService;
import com.gestorDeTurnos.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final UsuarioSistemaRepository repository;
  private final JwtService jwtService;
  private final UsuarioRepository clientes;
  private final PasswordEncoder encoder;
  public AuthServiceImpl(AuthenticationManager authenticationManager, UsuarioSistemaRepository repository, JwtService jwtService, UsuarioRepository clientes, PasswordEncoder encoder) {
    this.authenticationManager = authenticationManager; this.repository = repository; this.jwtService = jwtService; this.clientes = clientes; this.encoder = encoder;
  }
  @Override public LoginResponseDTO login(LoginRequestDTO request) {
    try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password())); }
    catch (Exception exception) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"); }
    UsuarioSistema user = repository.findByEmailIgnoreCase(request.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
    return response(user);
  }
  @Override @Transactional
  public LoginResponseDTO registrar(RegistroRequestDTO request) {
    String email = request.email().trim().toLowerCase();
    if (repository.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
    Usuario cliente = clientes.findByEmailIgnoreCase(email).orElseGet(() -> clientes.save(Usuario.builder()
        .nombre(request.nombre().trim()).email(email).telefono(request.telefono()).build()));
    cliente.setNombre(request.nombre().trim());
    cliente.setTelefono(request.telefono());
    UsuarioSistema user = repository.save(UsuarioSistema.builder().nombre(request.nombre().trim()).email(email)
        .password(encoder.encode(request.password())).rol(RolSistema.CLIENTE).cliente(cliente).build());
    return response(user);
  }
  private LoginResponseDTO response(UsuarioSistema user) {
    return new LoginResponseDTO(jwtService.generateToken(user), "Bearer", jwtService.getExpirationSeconds(), user.getNombre(), user.getRol().name(), user.getCliente() == null ? null : user.getCliente().getId());
  }
}

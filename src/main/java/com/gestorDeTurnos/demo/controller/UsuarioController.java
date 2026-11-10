package com.gestorDeTurnos.demo.controller;

import com.gestorDeTurnos.demo.dto.UsuarioRequestDTO;
import com.gestorDeTurnos.demo.dto.ClienteResponseDTO;
import com.gestorDeTurnos.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints administrativos para consultar y mantener perfiles de cliente. */
@RestController
@RequestMapping("/api/v1/clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {
  private final UsuarioService service;

  public UsuarioController(UsuarioService service) {
    this.service = service;
  }

  @GetMapping
  public List<ClienteResponseDTO> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ClienteResponseDTO obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClienteResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  public ClienteResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}

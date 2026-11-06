package com.gestorDeTurnos.demo.controller;
import com.gestorDeTurnos.demo.dto.*;
import com.gestorDeTurnos.demo.service.ReservaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.security.Principal;
@RestController @RequestMapping("/api/v1/reservas") @SecurityRequirement(name="Bearer Authentication")
public class ReservaController {
  private final ReservaService service;
  public ReservaController(ReservaService service){this.service=service;}
  @PostMapping @ResponseStatus(HttpStatus.CREATED) public ReservaResponseDTO crear(@Valid @RequestBody ReservaRequestDTO request){return service.crear(request);}
  @PostMapping("/solicitudes") @ResponseStatus(HttpStatus.CREATED) public ReservaResponseDTO solicitar(@Valid @RequestBody SolicitudReservaRequestDTO request, Principal principal){return service.solicitar(principal.getName(), request);}
  @GetMapping("/mis-reservas") public List<ReservaResponseDTO> misReservas(Principal principal){return service.misReservas(principal.getName());}
  @GetMapping public List<ReservaResponseDTO> porCanchaYFecha(@RequestParam Long canchaId,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha){return service.porCanchaFecha(canchaId,fecha);}
  @GetMapping("/cliente/{usuarioId}") public List<ReservaResponseDTO> porUsuario(@PathVariable Long usuarioId){return service.porUsuario(usuarioId);}
  @PatchMapping("/{id}/cancelar") public ReservaResponseDTO cancelar(@PathVariable Long id){return service.cancelar(id);}
}

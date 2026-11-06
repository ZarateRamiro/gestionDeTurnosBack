package com.gestorDeTurnos.demo.controller;
import com.gestorDeTurnos.demo.dto.CanchaRequestDTO;
import com.gestorDeTurnos.demo.dto.CanchaResponseDTO;
import com.gestorDeTurnos.demo.service.CanchaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/canchas")
public class CanchaController {
  private final CanchaService service;
  public CanchaController(CanchaService service){this.service=service;}
  @GetMapping public List<CanchaResponseDTO> listar(){return service.listar();}
  @GetMapping("/{id}") public CanchaResponseDTO obtener(@PathVariable Long id){return service.obtener(id);}
  @PostMapping @ResponseStatus(HttpStatus.CREATED) @SecurityRequirement(name="Bearer Authentication") public CanchaResponseDTO crear(@Valid @RequestBody CanchaRequestDTO request){return service.crear(request);}
  @PutMapping("/{id}") @SecurityRequirement(name="Bearer Authentication") public CanchaResponseDTO actualizar(@PathVariable Long id,@Valid @RequestBody CanchaRequestDTO request){return service.actualizar(id,request);}
  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @SecurityRequirement(name="Bearer Authentication") public void desactivar(@PathVariable Long id){service.desactivar(id);}
}

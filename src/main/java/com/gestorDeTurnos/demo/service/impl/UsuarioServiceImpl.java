package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.dto.UsuarioRequestDTO;
import com.gestorDeTurnos.demo.dto.ClienteResponseDTO;
import com.gestorDeTurnos.demo.model.Usuario;
import com.gestorDeTurnos.demo.repository.ReservaRepository;
import com.gestorDeTurnos.demo.repository.UsuarioRepository;
import com.gestorDeTurnos.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/** Aplica reglas de clientes y evita borrar personas que tengan reservas históricas asociadas. */
@Service
public class UsuarioServiceImpl implements UsuarioService {
  private final UsuarioRepository repo;
  private final ReservaRepository reservaRepository;
  public UsuarioServiceImpl(UsuarioRepository repo, ReservaRepository reservaRepository) { this.repo = repo; this.reservaRepository = reservaRepository; }
  @Override public List<ClienteResponseDTO> listar() { return repo.findAll().stream().map(this::toResponse).toList(); }
  @Override public ClienteResponseDTO crear(UsuarioRequestDTO r) {
    if (repo.existsByEmailIgnoreCase(r.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese email");
    return toResponse(repo.save(Usuario.builder().nombre(r.nombre()).email(r.email().trim().toLowerCase()).telefono(r.telefono()).build()));
  }
  @Override public ClienteResponseDTO obtener(Long id) { return toResponse(findEntity(id)); }
  @Override public ClienteResponseDTO actualizar(Long id, UsuarioRequestDTO r) {
    Usuario user = findEntity(id);
    if (repo.existsByEmailIgnoreCaseAndIdNot(r.email(), id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro cliente con ese email");
    user.setNombre(r.nombre()); user.setEmail(r.email().trim().toLowerCase()); user.setTelefono(r.telefono());
    return toResponse(repo.save(user));
  }
  @Override public void eliminar(Long id) {
    Usuario user = findEntity(id);
    // La restricción conserva la integridad referencial y el historial de turnos.
    if (reservaRepository.existsByUsuarioId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el cliente porque tiene reservas asociadas");
    repo.delete(user);
  }
  private Usuario findEntity(Long id) { return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")); }
  private ClienteResponseDTO toResponse(Usuario user) { return new ClienteResponseDTO(user.getId(), user.getNombre(), user.getEmail(), user.getTelefono(), user.getFechaAlta()); }
}

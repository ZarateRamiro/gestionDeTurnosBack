package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.UsuarioRequestDTO;
import com.gestorDeTurnos.demo.dto.ClienteResponseDTO;
import java.util.List;

public interface UsuarioService {
  List<ClienteResponseDTO> listar();
  ClienteResponseDTO crear(UsuarioRequestDTO request);
  ClienteResponseDTO obtener(Long id);
  ClienteResponseDTO actualizar(Long id, UsuarioRequestDTO request);
  void eliminar(Long id);
}

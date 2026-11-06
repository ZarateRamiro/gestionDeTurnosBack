package com.gestorDeTurnos.demo.service.impl;

import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioSistemaDetailsService implements UserDetailsService {
  private final UsuarioSistemaRepository repository;
  public UsuarioSistemaDetailsService(UsuarioSistemaRepository repository) { this.repository = repository; }
  @Override public UserDetails loadUserByUsername(String email) {
    return repository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UsernameNotFoundException("Cuenta de acceso no encontrada"));
  }
}

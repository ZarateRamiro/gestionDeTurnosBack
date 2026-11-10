package com.gestorDeTurnos.demo.repository;

import com.gestorDeTurnos.demo.model.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acceso a las cuentas que pueden autenticarse en la aplicación. */
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
  Optional<UsuarioSistema> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
}

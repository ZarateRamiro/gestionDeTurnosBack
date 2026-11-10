package com.gestorDeTurnos.demo.repository;
import com.gestorDeTurnos.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Consultas de persistencia para clientes; Spring Data implementa estos métodos por convención. */
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
  boolean existsByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
  Optional<Usuario> findByEmailIgnoreCase(String email);
}

package com.gestorDeTurnos.demo.repository;
import com.gestorDeTurnos.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
  boolean existsByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
  Optional<Usuario> findByEmailIgnoreCase(String email);
}

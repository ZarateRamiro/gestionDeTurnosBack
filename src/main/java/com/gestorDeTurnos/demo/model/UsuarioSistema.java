package com.gestorDeTurnos.demo.model;

import com.gestorDeTurnos.demo.enums.RolSistema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/** Cuenta de autenticación separada del perfil de cliente. */
@Entity
@Table(name = "usuarios_sistema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioSistema implements UserDetails {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private RolSistema rol;

  // El perfil se consulta únicamente en los flujos que necesitan datos del cliente.
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id", unique = true)
  private Usuario cliente;

  @Override public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
  }
  @Override public String getUsername() { return email; }
  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}

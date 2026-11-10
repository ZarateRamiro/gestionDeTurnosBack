package com.gestorDeTurnos.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Perfil de contacto del cliente; sus credenciales están en UsuarioSistema. */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 100)
  private String nombre;
  @Column(nullable = false, unique = true, length = 150)
  private String email;
  @Column(length = 30)
  private String telefono;
  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime fechaAlta = LocalDateTime.now();
}

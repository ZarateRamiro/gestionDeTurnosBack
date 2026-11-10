package com.gestorDeTurnos.demo.config;

import com.gestorDeTurnos.demo.enums.RolSistema;
import com.gestorDeTurnos.demo.model.UsuarioSistema;
import com.gestorDeTurnos.demo.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crea el administrador inicial solo cuando las credenciales llegan desde la configuración del entorno. */
@Component
public class AdminBootstrap implements CommandLineRunner {
  private final UsuarioSistemaRepository repository;
  private final PasswordEncoder encoder;
  private final String email;
  private final String password;
  private final String name;

  public AdminBootstrap(UsuarioSistemaRepository repository, PasswordEncoder encoder,
      @Value("${app.admin.email:}") String email,
      @Value("${app.admin.password:}") String password,
      @Value("${app.admin.name:Administrador}") String name) {
    this.repository = repository; this.encoder = encoder; this.email = email; this.password = password; this.name = name;
  }

  @Override public void run(String... args) {
    // Evita incluir credenciales por defecto en el código o crear un acceso accidental.
    if (email.isBlank() || password.isBlank()) {
      System.out.println("No se creó cuenta administradora: configurá ADMIN_EMAIL y ADMIN_PASSWORD para habilitar el login.");
      return;
    }
    if (password.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD debe tener al menos 12 caracteres.");
    // BCrypt transforma la clave antes de persistirla; nunca se guarda en texto plano.
    if (!repository.existsByEmailIgnoreCase(email)) {
      repository.save(UsuarioSistema.builder().nombre(name).email(email.trim().toLowerCase())
          .password(encoder.encode(password)).rol(RolSistema.ADMINISTRADOR).build());
      System.out.println("Cuenta administradora inicial creada para " + email + ".");
    }
  }
}

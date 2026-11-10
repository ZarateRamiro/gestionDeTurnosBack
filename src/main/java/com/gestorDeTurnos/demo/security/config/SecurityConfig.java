package com.gestorDeTurnos.demo.security.config;

import com.gestorDeTurnos.demo.security.jwt.JwtAuthFilter;
import com.gestorDeTurnos.demo.service.impl.UsuarioSistemaDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/** Declara filtros, permisos por endpoint, sesiones stateless y CORS. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        // La API no mantiene sesión en servidor: cada petición protegida lleva su JWT.
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) -> response.sendError(401)))
        .authorizeHttpRequests(auth -> auth
            // Rutas públicas; el resto de /api/v1 se autoriza por rol en las reglas siguientes.
            .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/canchas", "/api/v1/canchas/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/disponibilidad").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/reservas/mis-reservas").hasRole("CLIENTE")
            .requestMatchers(HttpMethod.POST, "/api/v1/reservas/solicitudes").hasRole("CLIENTE")
            .requestMatchers("/api/v1/**").hasRole("ADMINISTRADOR")
            .anyRequest().permitAll())
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean AuthenticationProvider authenticationProvider(UsuarioSistemaDetailsService detailsService, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(detailsService);
    provider.setPasswordEncoder(encoder);
    return provider;
  }

  @Bean AuthenticationManager authenticationManager(AuthenticationProvider provider) { return new ProviderManager(provider); }
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:4200",
        "http://127.0.0.1:4200",
        "http://localhost:8080",
        "http://127.0.0.1:8080"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
    config.setExposedHeaders(List.of("Authorization"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}

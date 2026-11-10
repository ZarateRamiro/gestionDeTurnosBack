package com.gestorDeTurnos.demo.model;

import com.gestorDeTurnos.demo.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Una reserva vincula una cancha con un cliente para una fecha y franja horaria. */
@Entity @Table(name="reservas", indexes={@Index(name="idx_reserva_cancha_fecha",columnList="cancha_id,fecha")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {
  // La base genera el ID al insertar; las relaciones se cargan bajo demanda.
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  // LAZY evita traer cancha y cliente cuando el caso de uso no los necesita.
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cancha_id",nullable=false) private Cancha cancha;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
  @Column(nullable=false) private LocalDate fecha;
  @Column(nullable=false) private LocalTime horaInicio;
  @Column(nullable=false) private LocalTime horaFin;
  // Guarda el nombre del estado, no su posición numérica en el enum.
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) @Builder.Default private EstadoReserva estado=EstadoReserva.PENDIENTE;
  @Column(nullable=false,precision=10,scale=2) private BigDecimal precioTotal;
  @Column(length=500) private String observaciones;
  @Builder.Default @Column(nullable=false,updatable=false) private LocalDateTime creadaEn=LocalDateTime.now();
}

package com.gestorDeTurnos.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity @Table(name="canchas") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cancha {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,length=100) private String nombre;
  @Column(nullable=false,length=150) private String direccion;
  @Column(length=30) private String tipoSuperficie;
  @Column(nullable=false) private Integer capacidadJugadores;
  @Column(nullable=false,precision=10,scale=2) private BigDecimal precioPorHora;
  @Builder.Default @Column(nullable=false) private Boolean activa=true;
  @Builder.Default @Column(name="hora_apertura") private LocalTime horaApertura=LocalTime.of(8, 0);
  @Builder.Default @Column(name="hora_cierre") private LocalTime horaCierre=LocalTime.of(23, 0);
}

package com.gestorDeTurnos.demo.dto;
import com.gestorDeTurnos.demo.enums.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
/** Respuesta API sin serializar entidades ni proxies JPA. */
public record ReservaResponseDTO(Long id,Long canchaId,String cancha,Long usuarioId,String cliente,LocalDate fecha,LocalTime horaInicio,LocalTime horaFin,EstadoReserva estado,BigDecimal precioTotal,String observaciones,LocalDateTime creadaEn) {}

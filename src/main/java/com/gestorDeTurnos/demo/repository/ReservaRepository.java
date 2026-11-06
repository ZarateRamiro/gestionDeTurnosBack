package com.gestorDeTurnos.demo.repository;
import com.gestorDeTurnos.demo.enums.EstadoReserva;
import com.gestorDeTurnos.demo.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
  boolean existsByUsuarioId(Long usuarioId);
  @Query("select (count(r)>0) from Reserva r where r.cancha.id=:canchaId and r.fecha=:fecha and r.estado<>:cancelada and r.horaInicio<:fin and r.horaFin>:inicio")
  boolean existeSuperposicion(@Param("canchaId") Long canchaId,@Param("fecha") LocalDate fecha,@Param("inicio") LocalTime inicio,@Param("fin") LocalTime fin,@Param("cancelada") EstadoReserva cancelada);
  List<Reserva> findByCanchaIdAndFechaAndEstadoNotOrderByHoraInicio(Long canchaId,LocalDate fecha,EstadoReserva estado);
  List<Reserva> findByUsuarioIdOrderByFechaDescHoraInicioDesc(Long usuarioId);
}

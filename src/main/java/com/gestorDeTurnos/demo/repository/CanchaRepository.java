package com.gestorDeTurnos.demo.repository;

import com.gestorDeTurnos.demo.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data implementa el CRUD y la consulta derivada de canchas activas. */
public interface CanchaRepository extends JpaRepository<Cancha, Long> {
  List<Cancha> findByActivaTrue();
}

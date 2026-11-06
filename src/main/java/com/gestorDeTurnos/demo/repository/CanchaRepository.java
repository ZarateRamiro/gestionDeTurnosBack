package com.gestorDeTurnos.demo.repository;
import com.gestorDeTurnos.demo.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CanchaRepository extends JpaRepository<Cancha,Long> { List<Cancha> findByActivaTrue(); }

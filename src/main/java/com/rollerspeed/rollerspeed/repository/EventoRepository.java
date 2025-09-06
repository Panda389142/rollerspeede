package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByActivoTrueOrderByFechaEventoAsc();

    List<Evento> findByFechaEventoAfterAndActivoTrueOrderByFechaEventoAsc(LocalDateTime fecha);
}

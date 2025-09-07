package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Asistencia;
import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByUsuarioAndClaseAndFecha(Usuario usuario, Clase clase, LocalDate fecha);

    @Query("SELECT a FROM Asistencia a JOIN FETCH a.clase WHERE a.usuario = :usuario ORDER BY a.fecha DESC")
    List<Asistencia> findByUsuarioOrderByFechaDesc(@Param("usuario") Usuario usuario);

    List<Asistencia> findByClaseOrderByFechaDesc(Clase clase);

    List<Asistencia> findByFechaOrderByClaseAsc(LocalDate fecha);

    @Query("SELECT a FROM Asistencia a WHERE a.usuario.id = :usuarioId AND a.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fecha DESC")
    List<Asistencia> findAsistenciasByUsuarioAndPeriodo(@Param("usuarioId") Long usuarioId, 
                                                       @Param("fechaInicio") LocalDate fechaInicio,
                                                       @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT a FROM Asistencia a WHERE a.clase.id = :claseId AND a.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fecha DESC")
    List<Asistencia> findAsistenciasByClaseAndPeriodo(@Param("claseId") Long claseId,
                                                      @Param("fechaInicio") LocalDate fechaInicio,
                                                      @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.usuario.id = :usuarioId AND a.presente = true")
    long countAsistenciasPresentes(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.usuario.id = :usuarioId")
    long countTotalAsistencias(@Param("usuarioId") Long usuarioId);
}
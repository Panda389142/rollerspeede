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

    Optional<Asistencia> findByAlumnoAndClaseAndFecha(Usuario alumno, Clase clase, LocalDate fecha);

    List<Asistencia> findByAlumnoOrderByFechaDesc(Usuario alumno);

    List<Asistencia> findByClaseOrderByFechaDesc(Clase clase);

    List<Asistencia> findByFechaOrderByClaseAsc(LocalDate fecha);

    @Query("SELECT a FROM Asistencia a WHERE a.alumno.id = :alumnoId AND a.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fecha DESC")
    List<Asistencia> findAsistenciasByAlumnoAndPeriodo(@Param("alumnoId") Long alumnoId, 
                                                       @Param("fechaInicio") LocalDate fechaInicio,
                                                       @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT a FROM Asistencia a WHERE a.clase.id = :claseId AND a.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fecha DESC")
    List<Asistencia> findAsistenciasByClaseAndPeriodo(@Param("claseId") Long claseId,
                                                      @Param("fechaInicio") LocalDate fechaInicio,
                                                      @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.alumno.id = :alumnoId AND a.presente = true")
    long countAsistenciasPresentes(@Param("alumnoId") Long alumnoId);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.alumno.id = :alumnoId")
    long countTotalAsistencias(@Param("alumnoId") Long alumnoId);
}
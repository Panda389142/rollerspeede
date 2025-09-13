package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    List<Clase> findByActivaTrueOrderByDiaSemanaAscHoraInicioAsc();

    List<Clase> findByInstructorAndActivaTrue(Usuario instructor);

    List<Clase> findByNivelAndActivaTrue(Clase.Nivel nivel);

    List<Clase> findByDiaSemanaAndActivaTrue(Clase.DiaSemana diaSemana);

    @Query("SELECT c FROM Clase c WHERE c.activa = true AND SIZE(c.alumnos) < c.capacidadMaxima ORDER BY c.diaSemana, c.horaInicio")
    List<Clase> findClasesConCupo();

    @Query("SELECT c FROM Clase c JOIN c.alumnos a WHERE a.id = :alumnoId AND c.activa = true")
    List<Clase> findClasesByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT c FROM Clase c WHERE c.instructor.id = :instructorId AND c.activa = true ORDER BY c.diaSemana, c.horaInicio")
    List<Clase> findClasesByInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT c.id FROM Clase c JOIN c.alumnos a WHERE a.id = :alumnoId")
    Set<Long> findIdClasesByAlumnoId(@Param("alumnoId") Long alumnoId);

    @Query("SELECT DISTINCT c FROM Clase c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.alumnos WHERE c.activa = true ORDER BY c.diaSemana, c.horaInicio")
    List<Clase> findAllActiveWithDetails();
}
package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Asistencia;
import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import com.rollerspeed.rollerspeed.repository.UsuarioRepository;
import com.rollerspeed.rollerspeed.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClaseRepository claseRepository;

    public Asistencia registrarAsistencia(Asistencia asistencia) {
        // Verificar si ya existe una asistencia para el mismo alumno, clase y fecha
        Optional<Asistencia> existente = asistenciaRepository.findByAlumnoAndClaseAndFecha(
                asistencia.getAlumno(), 
                asistencia.getClase(), 
                asistencia.getFecha()
        );
        
        if (existente.isPresent()) {
            // Actualizar la asistencia existente
            Asistencia asistenciaExistente = existente.get();
            asistenciaExistente.setPresente(asistencia.getPresente());
            asistenciaExistente.setObservaciones(asistencia.getObservaciones());
            asistenciaExistente.setRegistradoPor(asistencia.getRegistradoPor());
            return asistenciaRepository.save(asistenciaExistente);
        } else {
            // Crear nueva asistencia
            return asistenciaRepository.save(asistencia);
        }
    }

    public Optional<Asistencia> buscarAsistencia(Usuario alumno, Clase clase, LocalDate fecha) {
        return asistenciaRepository.findByAlumnoAndClaseAndFecha(alumno, clase, fecha);
    }

    public List<Asistencia> listarAsistenciasPorAlumno(Usuario alumno) {
        return asistenciaRepository.findByAlumnoOrderByFechaDesc(alumno);
    }

    public List<Asistencia> listarAsistenciasPorClase(Clase clase) {
        return asistenciaRepository.findByClaseOrderByFechaDesc(clase);
    }

    public List<Asistencia> listarAsistenciasPorFecha(LocalDate fecha) {
        return asistenciaRepository.findByFechaOrderByClaseAsc(fecha);
    }

    public List<Asistencia> obtenerAsistenciasEnPeriodo(Long alumnoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.findAsistenciasByAlumnoAndPeriodo(alumnoId, fechaInicio, fechaFin);
    }

    public List<Asistencia> obtenerAsistenciasClaseEnPeriodo(Long claseId, LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.findAsistenciasByClaseAndPeriodo(claseId, fechaInicio, fechaFin);
    }

    public double calcularPorcentajeAsistencia(Long alumnoId) {
        long totalAsistencias = asistenciaRepository.countTotalAsistencias(alumnoId);
        if (totalAsistencias == 0) {
            return 0.0;
        }
        
        long asistenciasPresentes = asistenciaRepository.countAsistenciasPresentes(alumnoId);
        return (double) asistenciasPresentes / totalAsistencias * 100.0;
    }

    public void marcarAsistencia(Long alumnoId, Long claseId, LocalDate fecha, boolean presente, 
                                String observaciones, Usuario registradoPor) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        Optional<Asistencia> asistenciaOpt = asistenciaRepository.findByAlumnoAndClaseAndFecha(alumno, clase, fecha);

        Asistencia asistencia = asistenciaOpt.orElse(new Asistencia());
        asistencia.setAlumno(alumno);
        asistencia.setClase(clase);
        asistencia.setFecha(fecha);
        asistencia.setPresente(presente);
        asistencia.setObservaciones(observaciones);
        asistencia.setRegistradoPor(registradoPor);

        asistenciaRepository.save(asistencia);
    }

    public boolean actualizarAsistencia(Long asistenciaId, boolean presente, String observaciones) {
        Optional<Asistencia> asistenciaOpt = asistenciaRepository.findById(asistenciaId);
        
        if (asistenciaOpt.isPresent()) {
            Asistencia asistencia = asistenciaOpt.get();
            asistencia.setPresente(presente);
            asistencia.setObservaciones(observaciones);
            asistenciaRepository.save(asistencia);
            return true;
        }
        
        return false;
    }

    public void eliminarAsistencia(Long asistenciaId) {
        asistenciaRepository.deleteById(asistenciaId);
    }

    public long contarTotalAsistencias() {
        return asistenciaRepository.count();
    }

    public List<Asistencia> generarReporteAsistenciaMensual(int mes, int año) {
        LocalDate inicio = LocalDate.of(año, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
        
        return asistenciaRepository.findAll().stream()
                .filter(a -> !a.getFecha().isBefore(inicio) && !a.getFecha().isAfter(fin))
                .toList();
    }
}
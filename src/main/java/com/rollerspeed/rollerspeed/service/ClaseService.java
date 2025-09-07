package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    public Clase crearClase(Clase clase) {
        return claseRepository.save(clase);
    }

    public Clase actualizarClase(Clase clase) {
        return claseRepository.save(clase);
    }

    public Optional<Clase> buscarPorId(Long id) {
        return claseRepository.findById(id);
    }

    public List<Clase> listarTodasLasClases() {
        return claseRepository.findAll(); // Changed to findAll() for admin view
    }

    public List<Clase> listarClasesConCupo() {
        return claseRepository.findClasesConCupo();
    }

    public List<Clase> listarClasesPorInstructor(Usuario instructor) {
        return claseRepository.findByInstructorAndActivaTrue(instructor);
    }

    public List<Clase> listarClasesPorNivel(Clase.Nivel nivel) {
        return claseRepository.findByNivelAndActivaTrue(nivel);
    }

    public List<Clase> listarClasesPorDia(Clase.DiaSemana dia) {
        return claseRepository.findByDiaSemanaAndActivaTrue(dia);
    }

    public List<Clase> listarClasesDeAlumno(Long alumnoId) {
        return claseRepository.findClasesByAlumno(alumnoId);
    }

    public boolean inscribirAlumno(Long claseId, Usuario alumno) {
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            
            if (clase.tieneCupo() && !clase.getAlumnos().contains(alumno)) {
                clase.getAlumnos().add(alumno);
                claseRepository.save(clase);
                return true;
            }
        }
        
        return false;
    }

    public boolean desinscribirAlumno(Long claseId, Usuario alumno) {
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            
            if (clase.getAlumnos().contains(alumno)) {
                clase.getAlumnos().remove(alumno);
                claseRepository.save(clase);
                return true;
            }
        }
        
        return false;
    }

    public void desactivarClase(Long id) {
        claseRepository.findById(id).ifPresent(clase -> {
            clase.setActiva(false);
            claseRepository.save(clase);
        });
    }

    public void activarClase(Long id) {
        claseRepository.findById(id).ifPresent(clase -> {
            clase.setActiva(true);
            claseRepository.save(clase);
        });
    }

    public boolean asignarInstructor(Long claseId, Usuario instructor) {
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent() && instructor.getRol() == Usuario.Rol.INSTRUCTOR) {
            Clase clase = claseOpt.get();
            clase.setInstructor(instructor);
            claseRepository.save(clase);
            return true;
        }
        
        return false;
    }

    public long contarTotalClases() {
        return claseRepository.count();
    }

    public boolean validarHorario(Clase nuevaClase) {
        List<Clase> clasesDelDia = claseRepository.findByDiaSemanaAndActivaTrue(nuevaClase.getDiaSemana());
        
        for (Clase clase : clasesDelDia) {
            if (clase.getId() != null && clase.getId().equals(nuevaClase.getId())) {
                continue; // Skip self when updating
            }
            
            // Check for time overlap
            if (hayConflictoDeHorario(nuevaClase, clase)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean hayConflictoDeHorario(Clase clase1, Clase clase2) {
        return clase1.getHoraInicio().isBefore(clase2.getHoraFin()) &&
               clase2.getHoraInicio().isBefore(clase1.getHoraFin());
    }
}
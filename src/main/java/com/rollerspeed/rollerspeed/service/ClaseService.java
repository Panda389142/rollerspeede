package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ClaseService {

    private static final Logger logger = LoggerFactory.getLogger(ClaseService.class);

    @Autowired
    private ClaseRepository claseRepository;

    public Clase crearClase(Clase clase) {
        logger.info("Creando clase: {}", clase.getNombre());
        return claseRepository.save(clase);
    }

    public Clase actualizarClase(Clase clase) {
        logger.info("Actualizando clase: {}", clase.getNombre());
        return claseRepository.save(clase);
    }

    public Optional<Clase> buscarPorId(Long id) {
        logger.info("Buscando clase por ID: {}", id);
        return claseRepository.findById(id);
    }

    public List<Clase> listarTodasLasClases() {
        logger.info("Listando todas las clases activas para la vista de usuario.");
        List<Clase> clases = claseRepository.findByActivaTrueOrderByDiaSemanaAscHoraInicioAsc();
        logger.info("Se encontraron {} clases activas.", clases.size());
        return clases;
    }

    public List<Clase> listarTodasLasClasesParaAdmin() {
        logger.info("Listando absolutamente todas las clases para la vista de admin.");
        return claseRepository.findAll(); // Devuelve todas, activas e inactivas.
    }

    public List<Clase> listarClasesConCupo() {
        logger.info("Listando clases con cupo disponible.");
        return claseRepository.findClasesConCupo();
    }

    public List<Clase> listarClasesPorInstructor(Usuario instructor) {
        logger.info("Listando clases para el instructor: {}", instructor.getEmail());
        return claseRepository.findByInstructorAndActivaTrue(instructor);
    }

    public List<Clase> listarClasesPorNivel(Clase.Nivel nivel) {
        logger.info("Listando clases por nivel: {}", nivel);
        return claseRepository.findByNivelAndActivaTrue(nivel);
    }

    public List<Clase> listarClasesPorDia(Clase.DiaSemana dia) {
        logger.info("Listando clases por día: {}", dia);
        return claseRepository.findByDiaSemanaAndActivaTrue(dia);
    }

    public List<Clase> listarClasesDeAlumno(Long alumnoId) {
        logger.info("Listando clases para el alumno ID: {}", alumnoId);
        return claseRepository.findClasesByAlumno(alumnoId);
    }

    public Set<Long> listarIdsClasesDeAlumno(Long alumnoId) {
        logger.info("Listando IDs de clases para el alumno ID: {}", alumnoId);
        return claseRepository.findIdClasesByAlumnoId(alumnoId);
    }

    public boolean inscribirAlumno(Long claseId, Usuario alumno) {
        logger.info("Inscribiendo alumno {} en clase {}", alumno.getEmail(), claseId);
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            
            if (clase.tieneCupo() && !clase.getAlumnos().contains(alumno)) {
                clase.getAlumnos().add(alumno);
                claseRepository.save(clase);
                logger.info("Alumno {} inscrito exitosamente en clase {}", alumno.getEmail(), claseId);
                return true;
            }
        } else {
            logger.warn("No se encontró la clase con ID {} para inscribir al alumno {}.");
        }
        
        return false;
    }

    public boolean desinscribirAlumno(Long claseId, Usuario alumno) {
        logger.info("Desinscribiendo alumno {} de clase {}", alumno.getEmail(), claseId);
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            
            if (clase.getAlumnos().contains(alumno)) {
                clase.getAlumnos().remove(alumno);
                claseRepository.save(clase);
                logger.info("Alumno {} desinscrito exitosamente de clase {}", alumno.getEmail(), claseId);
                return true;
            } else {
                logger.warn("No se pudo desinscribir al alumno {} de clase {}: no estaba inscrito.");
            }
        } else {
            logger.warn("No se encontró la clase con ID {} para desinscribir al alumno {}.");
        }
        
        return false;
    }

    public void desactivarClase(Long id) {
        logger.info("Desactivando clase con ID: {}");
        claseRepository.findById(id).ifPresent(clase -> {
            clase.setActiva(false);
            claseRepository.save(clase);
            logger.info("Clase {} desactivada.");
        });
    }

    public void activarClase(Long id) {
        logger.info("Activando clase con ID: {}");
        claseRepository.findById(id).ifPresent(clase -> {
            clase.setActiva(true);
            claseRepository.save(clase);
            logger.info("Clase {} activada.");
        });
    }

    public boolean asignarInstructor(Long claseId, Usuario instructor) {
        logger.info("Asignando instructor {} a clase {}");
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        
        if (claseOpt.isPresent() && instructor.getRol() == Usuario.Rol.INSTRUCTOR) {
            Clase clase = claseOpt.get();
            clase.setInstructor(instructor);
            claseRepository.save(clase);
            logger.info("Instructor {} asignado a clase {}.");
            return true;
        } else {
            logger.warn("No se pudo asignar instructor {} a clase {}: clase no encontrada o usuario no es instructor.");
        }
        
        return false;
    }

    public long contarTotalClases() {
        logger.info("Contando total de clases.");
        return claseRepository.count();
    }

    public boolean validarHorario(Clase nuevaClase) {
        logger.info("Validando horario para la clase: {}");
        List<Clase> clasesDelDia = claseRepository.findByDiaSemanaAndActivaTrue(nuevaClase.getDiaSemana());
        
        for (Clase clase : clasesDelDia) {
            if (clase.getId() != null && clase.getId().equals(nuevaClase.getId())) {
                continue; // Skip self when updating
            }
            
            // Check for time overlap
            if (hayConflictoDeHorario(nuevaClase, clase)) {
                logger.warn("Conflicto de horario detectado para la clase {} con la clase {}.");
                return false;
            }
        }
        logger.info("No se encontraron conflictos de horario para la clase {}.");
        return true;
    }

    private boolean hayConflictoDeHorario(Clase clase1, Clase clase2) {
        return clase1.getHoraInicio().isBefore(clase2.getHoraFin()) &&
               clase2.getHoraInicio().isBefore(clase1.getHoraFin());
    }
}

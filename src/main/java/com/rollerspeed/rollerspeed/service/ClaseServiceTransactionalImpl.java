package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import com.rollerspeed.rollerspeed.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class ClaseServiceTransactionalImpl implements ClaseServiceTransactional {

    private static final Logger logger = LoggerFactory.getLogger(ClaseServiceTransactionalImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Override
    @Transactional
    public void crearClasesDeProba() {
        logger.info("Intentando crear clases de prueba...");
        // Verificar que existan instructores
        var instructores = usuarioRepository.findByRol(Usuario.Rol.INSTRUCTOR);
        if (instructores.isEmpty()) {
            logger.warn("No se pueden crear clases: no hay instructores disponibles");
            return;
        }

        Usuario instructor1 = instructores.get(0);
        Usuario instructor2 = instructores.size() > 1 ? instructores.get(1) : instructor1;

        // Crear clases de prueba
        // Antes de eliminar clases, desvincularlas de los usuarios para evitar errores de FK
        usuarioRepository.findAll().forEach(usuario -> {
            if (usuario.getClases() != null) {
                usuario.getClases().clear();
                usuarioRepository.save(usuario);
            }
        });
        
        claseRepository.deleteAll(); // Clear existing classes to ensure fresh data
        if (claseRepository.count() == 0) {
            logger.info("No se encontraron clases existentes. Creando clases de prueba...");
            
            // Clase para principiantes
            Clase principiante = new Clase();
            principiante.setNombre("Patinaje Principiante");
            principiante.setDescripcion("Clase ideal para quienes están empezando en el mundo del patinaje. Aprenderás las técnicas básicas de equilibrio y movimiento.");
            principiante.setNivel(Clase.Nivel.PRINCIPIANTE);
            principiante.setDiaSemana(Clase.DiaSemana.LUNES);
            principiante.setHoraInicio(LocalTime.of(10, 0));
            principiante.setHoraFin(LocalTime.of(11, 30));
            principiante.setCapacidadMaxima(15);
            principiante.setInstructor(instructor1);
            principiante.setActiva(true);
            principiante.setPrecio(50000.0);
            claseRepository.save(principiante);
            logger.info("Clase creada: {} con precio {}", principiante.getNombre(), principiante.getPrecio());

            // Clase intermedia
            Clase intermedio = new Clase();
            intermedio.setNombre("Patinaje Intermedio");
            intermedio.setDescripcion("Para estudiantes que ya dominan los fundamentos. Trabajaremos técnicas más avanzadas y coreografías básicas.");
            intermedio.setNivel(Clase.Nivel.INTERMEDIO);
            intermedio.setDiaSemana(Clase.DiaSemana.MIERCOLES);
            intermedio.setHoraInicio(LocalTime.of(14, 0));
            intermedio.setHoraFin(LocalTime.of(15, 30));
            intermedio.setCapacidadMaxima(12);
            intermedio.setInstructor(instructor2);
            intermedio.setActiva(true);
            intermedio.setPrecio(65000.0);
            claseRepository.save(intermedio);
            logger.info("Clase creada: {} con precio {}", intermedio.getNombre(), intermedio.getPrecio());

            // Clase avanzada
            Clase avanzado = new Clase();
            avanzado.setNombre("Patinaje Avanzado");
            avanzado.setDescripcion("Nivel avanzado con técnicas complejas, saltos y preparación para competencias.");
            avanzado.setNivel(Clase.Nivel.AVANZADO);
            avanzado.setDiaSemana(Clase.DiaSemana.VIERNES);
            avanzado.setHoraInicio(LocalTime.of(16, 0));
            avanzado.setHoraFin(LocalTime.of(18, 0));
            avanzado.setCapacidadMaxima(8);
            avanzado.setInstructor(instructor1);
            avanzado.setActiva(true);
            avanzado.setPrecio(80000.0);
            claseRepository.save(avanzado);
            logger.info("Clase creada: {} con precio {}", avanzado.getNombre(), avanzado.getPrecio());

            // Clase competitiva
            Clase competitivo = new Clase();
            competitivo.setNombre("Patinaje Competitivo");
            competitivo.setDescripcion("Preparación intensiva para competencias nacionales e internacionales. Solo para patinadores experimentados.");
            competitivo.setNivel(Clase.Nivel.COMPETITIVO);
            competitivo.setDiaSemana(Clase.DiaSemana.SABADO);
            competitivo.setHoraInicio(LocalTime.of(8, 0));
            competitivo.setHoraFin(LocalTime.of(10, 0));
            competitivo.setCapacidadMaxima(6);
            competitivo.setInstructor(instructor2);
            competitivo.setActiva(true);
            competitivo.setPrecio(100000.0);
            claseRepository.save(competitivo);
            logger.info("Clase creada: {} con precio {}", competitivo.getNombre(), competitivo.getPrecio());

            // Clase para niños
            Clase ninos = new Clase();
            ninos.setNombre("Patinaje para Niños");
            ninos.setDescripcion("Clase especial para niños de 5 a 12 años. Aprendizaje divertido y seguro con juegos y actividades adaptadas.");
            ninos.setNivel(Clase.Nivel.PRINCIPIANTE);
            ninos.setDiaSemana(Clase.DiaSemana.SABADO);
            ninos.setHoraInicio(LocalTime.of(10, 30));
            ninos.setHoraFin(LocalTime.of(11, 30));
            ninos.setCapacidadMaxima(20);
            ninos.setInstructor(instructor1);
            ninos.setActiva(true);
            ninos.setPrecio(45000.0);
            claseRepository.save(ninos);
            logger.info("Clase creada: {} con precio {}", ninos.getNombre(), ninos.getPrecio());

            // Clase de fin de semana
            Clase dominical = new Clase();
            dominical.setNombre("Patinaje Recreativo");
            dominical.setDescripcion("Clase relajada para todos los niveles. Perfecta para quienes quieren patinar por diversión y ejercicio.");
            dominical.setNivel(Clase.Nivel.INTERMEDIO);
            dominical.setDiaSemana(Clase.DiaSemana.DOMINGO);
            dominical.setHoraInicio(LocalTime.of(9, 0));
            dominical.setHoraFin(LocalTime.of(10, 30));
            dominical.setCapacidadMaxima(25);
            dominical.setInstructor(instructor2);
            dominical.setActiva(true);
            dominical.setPrecio(40000.0);
            claseRepository.save(dominical);
            logger.info("Clase creada: {} con precio {}", dominical.getNombre(), dominical.getPrecio());

            logger.info("Clases de prueba creadas exitosamente");
        } else {
            logger.info("Ya existen clases en la base de datos. No se crearon clases de prueba.");
        }
    }
}

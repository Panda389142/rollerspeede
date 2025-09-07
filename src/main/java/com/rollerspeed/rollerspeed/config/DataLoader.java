package com.rollerspeed.rollerspeed.config;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.model.Testimonio;
import com.rollerspeed.rollerspeed.model.Noticia;
import com.rollerspeed.rollerspeed.model.Evento;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import com.rollerspeed.rollerspeed.repository.UsuarioRepository;
import com.rollerspeed.rollerspeed.repository.TestimonioRepository;
import com.rollerspeed.rollerspeed.repository.NoticiaRepository;
import com.rollerspeed.rollerspeed.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final UsuarioRepository usuarioRepository;
    private final ClaseRepository claseRepository;
    private final TestimonioRepository testimonioRepository;
    private final NoticiaRepository noticiaRepository;
    private final EventoRepository eventoRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection para evitar referencias circulares
    public DataLoader(UsuarioRepository usuarioRepository,
                     ClaseRepository claseRepository,
                     TestimonioRepository testimonioRepository,
                     NoticiaRepository noticiaRepository,
                     EventoRepository eventoRepository,
                     PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.claseRepository = claseRepository;
        this.testimonioRepository = testimonioRepository;
        this.noticiaRepository = noticiaRepository;
        this.eventoRepository = eventoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando carga de datos de prueba...");
        try {
            // Crear usuarios de prueba si no existen
            crearUsuariosDePrueba();
            crearClasesDePrueba();
            crearTestimoniosDePrueba();
            crearNoticiasDePrueba();
            crearEventosDePrueba();
            logger.info("Carga de datos de prueba finalizada exitosamente.");
        } catch (Exception e) {
            logger.error("Error durante la carga de datos de prueba: ", e);
            throw e;
        }
    }

    private void crearUsuariosDePrueba() {
        logger.info("Creando usuarios de prueba...");
        
        // Crear administrador
        if (!usuarioRepository.existsByEmail("admin@rollerspeed.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@rollerspeed.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador RollerSpeed");
            admin.setFechaNacimiento(LocalDate.of(1985, 1, 1));
            admin.setGenero(Usuario.Genero.MASCULINO);
            admin.setTelefono("3001234567");
            admin.setMedioPago(Usuario.MedioPago.EFECTIVO);
            admin.setRol(Usuario.Rol.ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            logger.info("Administrador creado: admin@rollerspeed.com / admin123");
        }

        // Crear instructor
        if (!usuarioRepository.existsByEmail("instructor@rollerspeed.com")) {
            Usuario instructor = new Usuario();
            instructor.setEmail("instructor@rollerspeed.com");
            instructor.setPassword(passwordEncoder.encode("instructor123"));
            instructor.setNombre("Carlos Mendoza");
            instructor.setFechaNacimiento(LocalDate.of(1990, 5, 15));
            instructor.setGenero(Usuario.Genero.MASCULINO);
            instructor.setTelefono("3009876543");
            instructor.setMedioPago(Usuario.MedioPago.TARJETA_CREDITO);
            instructor.setRol(Usuario.Rol.INSTRUCTOR);
            instructor.setActivo(true);
            usuarioRepository.save(instructor);
            logger.info("Instructor creado: instructor@rollerspeed.com / instructor123");
        }

        // Crear otro instructor
        if (!usuarioRepository.existsByEmail("maria.instructor@rollerspeed.com")) {
            Usuario instructor2 = new Usuario();
            instructor2.setEmail("maria.instructor@rollerspeed.com");
            instructor2.setPassword(passwordEncoder.encode("maria123"));
            instructor2.setNombre("María Rodríguez");
            instructor2.setFechaNacimiento(LocalDate.of(1988, 8, 22));
            instructor2.setGenero(Usuario.Genero.FEMENINO);
            instructor2.setTelefono("3007654321");
            instructor2.setMedioPago(Usuario.MedioPago.PSE);
            instructor2.setRol(Usuario.Rol.INSTRUCTOR);
            instructor2.setActivo(true);
            usuarioRepository.save(instructor2);
            logger.info("Instructor creado: maria.instructor@rollerspeed.com / maria123");
        }

        // Crear alumno de prueba
        if (!usuarioRepository.existsByEmail("alumno@test.com")) {
            Usuario alumno = new Usuario();
            alumno.setEmail("alumno@test.com");
            alumno.setPassword(passwordEncoder.encode("alumno123"));
            alumno.setNombre("Ana González");
            alumno.setFechaNacimiento(LocalDate.of(1995, 3, 10));
            alumno.setGenero(Usuario.Genero.FEMENINO);
            alumno.setTelefono("3005555555");
            alumno.setMedioPago(Usuario.MedioPago.NEQUI);
            alumno.setRol(Usuario.Rol.ALUMNO);
            alumno.setActivo(true);
            usuarioRepository.save(alumno);
            logger.info("Alumno creado: alumno@test.com / alumno123");
        }

        // Crear más alumnos de prueba
        if (!usuarioRepository.existsByEmail("juan@test.com")) {
            Usuario alumno2 = new Usuario();
            alumno2.setEmail("juan@test.com");
            alumno2.setPassword(passwordEncoder.encode("juan123"));
            alumno2.setNombre("Juan Pérez");
            alumno2.setFechaNacimiento(LocalDate.of(2005, 7, 18));
            alumno2.setGenero(Usuario.Genero.MASCULINO);
            alumno2.setTelefono("3006666666");
            alumno2.setMedioPago(Usuario.MedioPago.EFECTIVO);
            alumno2.setRol(Usuario.Rol.ALUMNO);
            alumno2.setActivo(true);
            usuarioRepository.save(alumno2);
            logger.info("Alumno creado: juan@test.com / juan123");
        }
    }

    private void crearClasesDePrueba() {
        // Para desarrollo: Limpiamos y volvemos a crear las clases para asegurar datos frescos.
        // En producción, esta lógica debería ser más cuidadosa.

        if (claseRepository.count() == 0) { // Only create if no classes exist
            logger.info("Creando nuevas clases de prueba...");
            
            // Buscar instructores
            Usuario instructor1 = usuarioRepository.findByEmail("instructor@rollerspeed.com").orElse(null);
            Usuario instructor2 = usuarioRepository.findByEmail("maria.instructor@rollerspeed.com").orElse(null);
            
            if (instructor1 == null || instructor2 == null) {
                logger.warn("No se pueden crear clases: instructores no disponibles");
                return;
            }

            try {
                // Clase 1 - Principiantes
                Clase clase1 = new Clase();
                clase1.setNombre("Patinaje Básico para Principiantes");
                clase1.setDescripcion("Aprende los fundamentos del patinaje en línea. Perfecto para quienes nunca han patinado.");
                clase1.setNivel(Clase.Nivel.PRINCIPIANTE);
                clase1.setDiaSemana(Clase.DiaSemana.LUNES);
                clase1.setHoraInicio(LocalTime.of(18, 0)); // 6:00 PM
                clase1.setHoraFin(LocalTime.of(19, 0));    // 7:00 PM
                clase1.setCapacidadMaxima(15);
                clase1.setPrecio(50000.0);
                clase1.setInstructor(instructor1);
                clase1.setActiva(true);
                claseRepository.save(clase1);
                logger.info("Clase creada: {} - Precio: ${}", clase1.getNombre(), clase1.getPrecio());

                // Clase 2 - Intermedio
                Clase clase2 = new Clase();
                clase2.setNombre("Patinaje Artístico Intermedio");
                clase2.setDescripcion("Técnicas avanzadas de patinaje artístico. Requiere experiencia previa.");
                clase2.setNivel(Clase.Nivel.INTERMEDIO);
                clase2.setDiaSemana(Clase.DiaSemana.MIERCOLES);
                clase2.setHoraInicio(LocalTime.of(19, 30)); // 7:30 PM
                clase2.setHoraFin(LocalTime.of(21, 0));     // 9:00 PM
                clase2.setCapacidadMaxima(10);
                clase2.setPrecio(75000.0);
                clase2.setInstructor(instructor2);
                clase2.setActiva(true);
                claseRepository.save(clase2);
                logger.info("Clase creada: {} - Precio: ${}", clase2.getNombre(), clase2.getPrecio());

                // Clase 3 - Velocidad
                Clase clase3 = new Clase();
                clase3.setNombre("Patinaje de Velocidad");
                clase3.setDescripcion("Entrena tu velocidad y resistencia en el patinaje. Para nivel intermedio-avanzado.");
                clase3.setNivel(Clase.Nivel.AVANZADO);
                clase3.setDiaSemana(Clase.DiaSemana.VIERNES);
                clase3.setHoraInicio(LocalTime.of(17, 0)); // 5:00 PM
                clase3.setHoraFin(LocalTime.of(19, 0));    // 7:00 PM
                clase3.setCapacidadMaxima(12);
                clase3.setPrecio(80000.0);
                clase3.setInstructor(instructor1);
                clase3.setActiva(true);
                claseRepository.save(clase3);
                logger.info("Clase creada: {} - Precio: ${}", clase3.getNombre(), clase3.getPrecio());

                // Clase 4 - Patinaje para Niños
                Clase clase4 = new Clase();
                clase4.setNombre("Patinaje para Niños");
                clase4.setDescripcion("Clases especializadas para niños de 6 a 12 años. Aprendizaje divertido y seguro.");
                clase4.setNivel(Clase.Nivel.PRINCIPIANTE);
                clase4.setDiaSemana(Clase.DiaSemana.SABADO);
                clase4.setHoraInicio(LocalTime.of(10, 0)); // 10:00 AM
                clase4.setHoraFin(LocalTime.of(10, 45));   // 10:45 AM
                clase4.setCapacidadMaxima(20);
                clase4.setPrecio(35000.0);
                clase4.setInstructor(instructor2);
                clase4.setActiva(true);
                claseRepository.save(clase4);
                logger.info("Clase creada: {} - Precio: ${}", clase4.getNombre(), clase4.getPrecio());

                // Clase 5 - Patinaje Recreativo
                Clase clase5 = new Clase();
                clase5.setNombre("Patinaje Recreativo Adultos");
                clase5.setDescripcion("Patinaje relajado para adultos. Ideal para hacer ejercicio y socializar.");
                clase5.setNivel(Clase.Nivel.PRINCIPIANTE);
                clase5.setDiaSemana(Clase.DiaSemana.MARTES);
                clase5.setHoraInicio(LocalTime.of(20, 0)); // 8:00 PM
                clase5.setHoraFin(LocalTime.of(21, 15));   // 9:15 PM
                clase5.setCapacidadMaxima(18);
                clase5.setPrecio(45000.0);
                clase5.setInstructor(instructor1);
                clase5.setActiva(true);
                claseRepository.save(clase5);
                logger.info("Clase creada: {} - Precio: ${}", clase5.getNombre(), clase5.getPrecio());

                // Clase 6 - Competitivo
                Clase clase6 = new Clase();
                clase6.setNombre("Entrenamientos Competitivos");
                clase6.setDescripcion("Preparación para competencias nacionales e internacionales. Solo por invitación.");
                clase6.setNivel(Clase.Nivel.COMPETITIVO);
                clase6.setDiaSemana(Clase.DiaSemana.JUEVES);
                clase6.setHoraInicio(LocalTime.of(18, 30)); // 6:30 PM
                clase6.setHoraFin(LocalTime.of(20, 30));    // 8:30 PM
                clase6.setCapacidadMaxima(8);
                clase6.setPrecio(120000.0);
                clase6.setInstructor(instructor2);
                clase6.setActiva(true);
                claseRepository.save(clase6);
                logger.info("Clase creada: {} - Precio: ${}", clase6.getNombre(), clase6.getPrecio());

                // Clase 7 - Domingo Familiar
                Clase clase7 = new Clase();
                clase7.setNombre("Patinaje Familiar");
                clase7.setDescripcion("Clases familiares donde padres e hijos pueden aprender juntos.");
                clase7.setNivel(Clase.Nivel.PRINCIPIANTE);
                clase7.setDiaSemana(Clase.DiaSemana.DOMINGO);
                clase7.setHoraInicio(LocalTime.of(11, 0)); // 11:00 AM
                clase7.setHoraFin(LocalTime.of(12, 0));    // 12:00 PM
                clase7.setCapacidadMaxima(25);
                clase7.setPrecio(40000.0);
                clase7.setInstructor(instructor1);
                clase7.setActiva(true);
                claseRepository.save(clase7);
                logger.info("Clase creada: {} - Precio: ${}", clase7.getNombre(), clase7.getPrecio());

                logger.info("Clases de prueba creadas exitosamente");
            } catch (Exception e) {
                logger.error("Error creando clases de prueba: ", e);
            }
        } // Fin del if
    }

    private void crearTestimoniosDePrueba() {
        if (testimonioRepository.count() == 0) {
            logger.info("Creando testimonios de prueba...");
            
            Testimonio testimonio1 = new Testimonio();
            testimonio1.setNombre("María González");
            testimonio1.setComentario("RollerSpeed cambió mi vida. Llegué sin saber patinar y ahora compito a nivel nacional. Los instructores son excepcionales.");
            testimonio1.setActivo(true);
            testimonioRepository.save(testimonio1);

            Testimonio testimonio2 = new Testimonio();
            testimonio2.setNombre("Carlos Mendoza");
            testimonio2.setComentario("Mi hijo encontró su pasión aquí. El ambiente es familiar y seguro. Recomiendo RollerSpeed a todos los padres.");
            testimonio2.setActivo(true);
            testimonioRepository.save(testimonio2);

            Testimonio testimonio3 = new Testimonio();
            testimonio3.setNombre("Ana Patricia Ruiz");
            testimonio3.setComentario("Nunca es tarde para aprender. A los 35 años decidí patinar y ha sido una experiencia increíble. ¡Altamente recomendado!");
            testimonio3.setActivo(true);
            testimonioRepository.save(testimonio3);

            logger.info("Testimonios de prueba creados exitosamente");
        } else {
            logger.info("Ya existen testimonios en la base de datos. No se crearon testimonios de prueba.");
        }
    }

    private void crearNoticiasDePrueba() {
        if (noticiaRepository.count() == 0) {
            logger.info("Creando noticias de prueba...");
            
            // Obtener el administrador como autor
            Usuario admin = usuarioRepository.findByEmail("admin@rollerspeed.com").orElse(null);
            if (admin == null) {
                logger.warn("No se pueden crear noticias: no hay administrador disponible");
                return;
            }

            Noticia noticia1 = new Noticia();
            noticia1.setTitulo("Nueva Temporada de Competencias");
            noticia1.setContenido("¡Atención patinadores! Ya está abierta la inscripción para la temporada 2025 de competencias locales y regionales. Prepárense para mostrar todo su talento.");
            noticia1.setAutor(admin);
            noticia1.setActivo(true);
            noticiaRepository.save(noticia1);

            Noticia noticia2 = new Noticia();
            noticia2.setTitulo("Clases Especiales de Verano");
            noticia2.setContenido("Durante las vacaciones de verano ofreceremos clases intensivas de patinaje artístico y velocidad. ¡No te quedes fuera!");
            noticia2.setAutor(admin);
            noticia2.setActivo(true);
            noticiaRepository.save(noticia2);

            Noticia noticia3 = new Noticia();
            noticia3.setTitulo("Nuevas Instalaciones");
            noticia3.setContenido("Estamos renovando nuestras pistas para ofrecerte la mejor experiencia de patinaje. Pronto tendremos nuevas superficies y equipamiento de última generación.");
            noticia3.setAutor(admin);
            noticia3.setActivo(true);
            noticiaRepository.save(noticia3);

            logger.info("Noticias de prueba creadas exitosamente");
        } else {
            logger.info("Ya existen noticias en la base de datos. No se crearon noticias de prueba.");
        }
    }

    private void crearEventosDePrueba() {
        if (eventoRepository.count() == 0) {
            logger.info("Creando eventos de prueba...");
            
            Evento evento1 = new Evento();
            evento1.setTitulo("Torneo Anual de Patinaje");
            evento1.setDescripcion("Gran torneo anual donde participarán patinadores de toda la región. ¡Ven a apoyar a nuestros estudiantes!");
            evento1.setFechaEvento(LocalDateTime.now().plusDays(30));
            evento1.setActivo(true);
            eventoRepository.save(evento1);

            Evento evento2 = new Evento();
            evento2.setTitulo("Día del Patinador");
            evento2.setDescripcion("Celebración especial con exhibiciones, clases gratuitas y actividades recreativas para toda la familia.");
            evento2.setFechaEvento(LocalDateTime.now().plusDays(60));
            evento2.setActivo(true);
            eventoRepository.save(evento2);

            Evento evento3 = new Evento();
            evento3.setTitulo("Workshop de Patinaje Artístico");
            evento3.setDescripcion("Taller especializado con instructores invitados internacionales. Cupos limitados.");
            evento3.setFechaEvento(LocalDateTime.now().plusDays(45));
            evento3.setActivo(true);
            eventoRepository.save(evento3);

            logger.info("Eventos de prueba creados exitosamente");
        } else {
            logger.info("Ya existen eventos en la base de datos. No se crearon eventos de prueba.");
        }
    }
}
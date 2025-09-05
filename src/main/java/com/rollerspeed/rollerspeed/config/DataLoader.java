package com.rollerspeed.rollerspeed.config;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import com.rollerspeed.rollerspeed.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuarios de prueba si no existen
        crearUsuariosDePrueba();
        crearClasesDePrueba();
    }

    private void crearUsuariosDePrueba() {
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
            System.out.println("Administrador creado: admin@rollerspeed.com / admin123");
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
            System.out.println("Instructor creado: instructor@rollerspeed.com / instructor123");
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
            System.out.println("Instructor creado: maria.instructor@rollerspeed.com / maria123");
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
            System.out.println("Alumno creado: alumno@test.com / alumno123");
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
        }
    }

    private void crearClasesDePrueba() {
        // Verificar que existan instructores
        var instructores = usuarioRepository.findByRol(Usuario.Rol.INSTRUCTOR);
        if (instructores.isEmpty()) {
            System.out.println("No se pueden crear clases: no hay instructores disponibles");
            return;
        }

        Usuario instructor1 = instructores.get(0);
        Usuario instructor2 = instructores.size() > 1 ? instructores.get(1) : instructor1;

        // Crear clases de prueba
        if (claseRepository.count() == 0) {
            
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
            claseRepository.save(principiante);

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
            claseRepository.save(intermedio);

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
            claseRepository.save(avanzado);

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
            claseRepository.save(competitivo);

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
            claseRepository.save(ninos);

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
            claseRepository.save(dominical);

            System.out.println("Clases de prueba creadas exitosamente");
        }
    }
}
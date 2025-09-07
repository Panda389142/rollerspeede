package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.Asistencia;
import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.service.AsistenciaService;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    @Autowired
    private ClaseService claseService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping("/clases/{id}/asistencia")
    public String mostrarFormularioAsistencia(@PathVariable Long id, Model model, Principal principal) {
        System.out.println("--- DEBUG: Ingresando a mostrarFormularioAsistencia ---");
        Clase clase = claseService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        Usuario instructor = usuarioService.buscarPorEmail(principal.getName()).orElseThrow();
        System.out.println("Instructor logueado: " + instructor.getNombre() + " (ID: " + instructor.getId() + ")");
        System.out.println("Clase: " + clase.getNombre() + " (ID: " + clase.getId() + ")");

        if (clase.getInstructor() != null) {
            System.out.println("Instructor de la clase: " + clase.getInstructor().getNombre() + " (ID: " + clase.getInstructor().getId() + ")");
        } else {
            System.out.println("Instructor de la clase: NULL");
        }

        // Verificación de seguridad: el instructor solo puede acceder a sus propias clases
        if (clase.getInstructor() == null || !clase.getInstructor().equals(instructor)) {
            System.out.println("--- DEBUG: ¡Acceso denegado! Redirigiendo a /error ---");
            return "redirect:/error"; // O una página de acceso denegado
        }

        List<Usuario> alumnos = new ArrayList<>(clase.getAlumnos());
        LocalDate hoy = LocalDate.now();

        model.addAttribute("clase", clase);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("fecha", hoy);
        // Obtenemos los IDs de los alumnos que ya tienen asistencia registrada como "presente" para hoy
        List<Long> presentesHoy = new ArrayList<>();
        if (clase.getAsistencias() != null) {
            presentesHoy = clase.getAsistencias().stream()
                    .filter(a -> a.getFecha().equals(hoy) && a.getPresente() && a.getAlumno() != null)
                    .map(a -> a.getAlumno().getId())
                    .collect(Collectors.toList());
        }
        model.addAttribute("presentesHoy", presentesHoy);

        return "instructor/tomar-asistencia"; // Asegúrate que la vista esté en esta ruta
    }

    @PostMapping("/clases/{claseId}/asistencia")
    public String guardarAsistencia(@PathVariable Long claseId,
                                    @RequestParam(required = false) List<Long> alumnosPresentes,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        Clase clase = claseService.buscarPorId(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));
        Usuario instructor = usuarioService.buscarPorEmail(principal.getName()).orElseThrow();

        for (Long alumnoId : clase.getAlumnos().stream().map(Usuario::getId).toList()) {
            boolean presente = alumnosPresentes != null && alumnosPresentes.contains(alumnoId);
            // Usamos el método que ya tienes en tu AsistenciaService
            asistenciaService.marcarAsistencia(alumnoId, claseId, LocalDate.now(), presente, "", instructor);
        }

        redirectAttributes.addFlashAttribute("mensaje", "Asistencia guardada correctamente para el " + LocalDate.now());
        return "redirect:/dashboard";
    }

        @GetMapping("/clases/{id}/asistencias/historial")
    public String verHistorialAsistencias(@PathVariable Long id, Model model, Principal principal) {
        System.out.println("--- DEBUG: Ingresando a verHistorialAsistencias ---");
        Clase clase = claseService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        Usuario instructor = usuarioService.buscarPorEmail(principal.getName()).orElseThrow();
        System.out.println("Instructor logueado: " + instructor.getNombre() + " (ID: " + instructor.getId() + ")");
        System.out.println("Clase: " + clase.getNombre() + " (ID: " + clase.getId() + ")");

        if (clase.getInstructor() != null) {
            System.out.println("Instructor de la clase: " + clase.getInstructor().getNombre() + " (ID: " + clase.getInstructor().getId() + ")");
        } else {
            System.out.println("Instructor de la clase: NULL");
        }

        // Verificación de seguridad
        if (clase.getInstructor() == null || !clase.getInstructor().equals(instructor)) {
            System.out.println("--- DEBUG: ¡Acceso denegado! Redirigiendo a /error ---");
            return "redirect:/error";
        }

        List<Asistencia> asistencias = asistenciaService.listarAsistenciasPorClase(clase);

        // Agrupar asistencias por alumno
        Map<Usuario, List<Asistencia>> asistenciasPorAlumno = asistencias.stream()
                .collect(Collectors.groupingBy(Asistencia::getAlumno));

        model.addAttribute("clase", clase);
        model.addAttribute("asistenciasPorAlumno", asistenciasPorAlumno);

        return "instructor/ver-asistencias";
    }

    @GetMapping("/clase/{id}/alumnos")
    public String verAlumnosDeClase(@PathVariable Long id, Model model, Principal principal) {
        Clase clase = claseService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada"));

        Usuario instructor = usuarioService.buscarPorEmail(principal.getName()).orElseThrow();

        // Verificación de seguridad: el instructor solo puede acceder a sus propias clases
        if (clase.getInstructor() == null || !clase.getInstructor().equals(instructor)) {
            return "redirect:/error"; // O una página de acceso denegado
        }

        model.addAttribute("clase", clase);
        model.addAttribute("alumnos", new ArrayList<>(clase.getAlumnos()));

        return "instructor/ver-alumnos";
    }
}
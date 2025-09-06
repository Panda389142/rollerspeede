package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import com.rollerspeed.rollerspeed.service.TestimonioService;
import com.rollerspeed.rollerspeed.service.PagoService;
import com.rollerspeed.rollerspeed.service.AsistenciaService;
import com.rollerspeed.rollerspeed.service.NoticiaService;
import com.rollerspeed.rollerspeed.service.EventoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private TestimonioService testimonioService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private NoticiaService noticiaService;

    @Autowired
    private EventoService eventoService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clases", claseService.listarClasesConCupo());
        model.addAttribute("testimonios", testimonioService.listarTestimoniosActivos());
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/";
    }

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros(Model model) {
        model.addAttribute("totalAlumnos", usuarioService.contarAlumnos());
        model.addAttribute("totalInstructores", usuarioService.contarInstructores());
        return "sobre-nosotros";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("clases", claseService.listarTodasLasClases());
        return "servicios";
    }

    @GetMapping("/galeria")
    public String galeria(Model model) {
        // Para la galería, podríamos agregar lógica para listar imágenes/videos dinámicos
        // Por ahora, mantenemos estático
        return "galeria";
    }

    @GetMapping("/testimonios")
    public String testimonios(Model model) {
        model.addAttribute("testimonios", testimonioService.listarTestimoniosActivos());
        return "testimonios";
    }

    @GetMapping("/noticias")
    public String noticias(Model model) {
        model.addAttribute("noticias", noticiaService.listarNoticiasActivas());
        model.addAttribute("eventos", eventoService.listarEventosFuturos());
        return "noticias";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    // Métodos para la autenticación y registro
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente.");
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute Usuario usuario,
                                BindingResult result,
                                Model model) { // Importante: Cambia RedirectAttributes por Model

        System.out.println("=== PROCESANDO REGISTRO ===");
        System.out.println("Usuario: " + usuario.getNombre());
        System.out.println("Email: " + usuario.getEmail());

        if (result.hasErrors()) {
            System.out.println("Errores de validación: " + result.getAllErrors());
            return "auth/registro"; // Devuelve la vista directamente
        }
        
        try {
            Usuario usuarioCreado = usuarioService.registrarUsuario(usuario);
            System.out.println("Usuario registrado con ID: " + usuarioCreado.getId());
            // En caso de éxito, redirige a login con un mensaje flash
            model.addAttribute("mensaje", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "auth/login";
        } catch (Exception e) {
            System.out.println("Error al registrar: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/registro"; // Devuelve la vista directamente con el error
        }
    }
    // ...

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'INSTRUCTOR', 'ALUMNO')")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);

            switch (usuario.getRol()) {
                case ADMINISTRADOR:
                    model.addAttribute("totalAlumnos", usuarioService.contarAlumnos());
                    model.addAttribute("totalInstructores", usuarioService.contarInstructores());
                    model.addAttribute("totalClases", claseService.contarTotalClases());
                    break;
                case INSTRUCTOR:
                    model.addAttribute("misClases", claseService.listarClasesPorInstructor(usuario));
                    break;
                case ALUMNO:
                    model.addAttribute("misClases", claseService.listarClasesDeAlumno(usuario.getId()));
                    model.addAttribute("clasesDisponibles", claseService.listarClasesConCupo());
                    break;
            }
        });

        return "dashboard";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'INSTRUCTOR', 'ALUMNO')")
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
        });

        return "perfil";
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'INSTRUCTOR', 'ALUMNO')")
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal,
                                  Authentication authentication) {
        try {
            String email = principal.getName();

            // 1. Cargar el usuario actual desde la base de datos
            Usuario usuarioActual = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 2. Actualizar solo los campos que son editables en el formulario
            usuarioActual.setNombre(usuarioForm.getNombre());
            usuarioActual.setTelefono(usuarioForm.getTelefono());

            // 3. Si el usuario es ADMINISTRADOR, permitir cambiar el método de pago
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))) {
                usuarioActual.setMedioPago(usuarioForm.getMedioPago());
            }

            // 4. Guardar el usuario actualizado.
            usuarioService.actualizarUsuario(usuarioActual);

            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PreAuthorize("hasAnyRole('ALUMNO', 'INSTRUCTOR', 'ADMINISTRADOR')")
    @GetMapping("/historial-pagos")
    public String historialPagos(Model model, Principal principal) {
        String email = principal.getName();
        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            model.addAttribute("pagos", pagoService.listarPagosPorAlumno(usuario));
        });
        return "historial-pagos";
    }

    @PreAuthorize("hasRole('ALUMNO')")
    @GetMapping("/mi-asistencia")
    public String miAsistencia(Model model, Principal principal) {
        String email = principal.getName();
        try {
            usuarioService.buscarPorEmail(email).ifPresentOrElse(usuario -> {
                model.addAttribute("asistencias", asistenciaService.listarAsistenciasPorAlumno(usuario));
                model.addAttribute("porcentajeAsistencia", asistenciaService.calcularPorcentajeAsistencia(usuario.getId()));
            }, () -> {
                // En el caso improbable de que no se encuentre el usuario, se envían valores por defecto.
                model.addAttribute("asistencias", java.util.Collections.emptyList());
                model.addAttribute("porcentajeAsistencia", 0.0);
                model.addAttribute("error", "No se pudo cargar la información del usuario.");
            });
        } catch (Exception e) {
            logger.error("Error al cargar la página de asistencia para el usuario: {}", email, e);
            // Capturamos cualquier excepción inesperada de los servicios para evitar que la página se rompa.
            model.addAttribute("asistencias", java.util.Collections.emptyList());
            model.addAttribute("porcentajeAsistencia", 0.0);
            model.addAttribute("error", "Ocurrió un error al calcular tus estadísticas de asistencia. Por favor, contacta a soporte.");
        }
        return "alumno/mi-asistencia";
    }
}
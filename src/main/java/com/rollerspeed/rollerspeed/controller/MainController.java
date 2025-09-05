package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClaseService claseService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clases", claseService.listarClasesConCupo());
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
    public String galeria() {
        return "galeria";
    }

    @GetMapping("/testimonios")
    public String testimonios() {
        return "testimonios";
    }

    @GetMapping("/noticias")
    public String noticias() {
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

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
        });

        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute Usuario usuario,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "perfil";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            usuarioService.buscarPorEmail(email).ifPresent(usuarioExistente -> {
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setTelefono(usuario.getTelefono());
                usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());
                usuarioExistente.setGenero(usuario.getGenero());
                usuarioExistente.setMedioPago(usuario.getMedioPago());
                usuarioService.actualizarUsuario(usuarioExistente);
            });

            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil");
        }

        return "redirect:/perfil";
    }

    @PostMapping("/inscribir-clase/{claseId}")
    @ResponseBody
    public String inscribirClase(@PathVariable Long claseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioService.buscarPorEmail(email)
                .map(usuario -> {
                    boolean exito = claseService.inscribirAlumno(claseId, usuario);
                    return exito ? "success" : "error";
                })
                .orElse("error");
    }
}
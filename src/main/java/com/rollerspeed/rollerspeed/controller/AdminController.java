package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.*;
import com.rollerspeed.rollerspeed.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    @Autowired
    private TestimonioService testimonioService;

    @Autowired
    private NoticiaService noticiaService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClaseService claseService;

    @GetMapping("/testimonios")
    public String listarTestimonios(Model model) {
        List<Testimonio> testimonios = testimonioService.listarTestimoniosActivos();
        model.addAttribute("testimonios", testimonios);
        return "admin/testimonios";
    }

    @GetMapping("/testimonios/nuevo")
    public String mostrarFormularioTestimonio(Model model) {
        model.addAttribute("testimonio", new Testimonio());
        return "admin/testimonio-form";
    }

    @PostMapping("/testimonios")
    public String guardarTestimonio(@Valid @ModelAttribute Testimonio testimonio,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/testimonio-form";
        }
        testimonioService.crearTestimonio(testimonio);
        redirectAttributes.addFlashAttribute("mensaje", "Testimonio guardado correctamente");
        return "redirect:/admin/testimonios";
    }

    @GetMapping("/testimonios/editar/{id}")
    public String mostrarFormularioEditarTestimonio(@PathVariable Long id, Model model) {
        Testimonio testimonio = testimonioService.buscarPorId(id).orElse(null);
        model.addAttribute("testimonio", testimonio);
        return "admin/testimonio-form";
    }

    @PostMapping("/testimonios/eliminar/{id}")
    public String eliminarTestimonio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        testimonioService.desactivarTestimonio(id);
        redirectAttributes.addFlashAttribute("mensaje", "Testimonio desactivado correctamente");
        return "redirect:/admin/testimonios";
    }

    @GetMapping("/noticias")
    public String listarNoticias(Model model) {
        List<Noticia> noticias = noticiaService.listarNoticiasActivas();
        model.addAttribute("noticias", noticias);
        return "admin/noticias";
    }

    @GetMapping("/noticias/nuevo")
    public String mostrarFormularioNoticia(Model model) {
        model.addAttribute("noticia", new Noticia());
        return "admin/noticia-form";
    }

    @PostMapping("/noticias")
    public String guardarNoticia(@Valid @ModelAttribute Noticia noticia,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/noticia-form";
        }
        noticiaService.crearNoticia(noticia);
        redirectAttributes.addFlashAttribute("mensaje", "Noticia guardada correctamente");
        return "redirect:/admin/noticias";
    }

    @GetMapping("/eventos")
    public String listarEventos(Model model) {
        List<Evento> eventos = eventoService.listarEventosActivos();
        model.addAttribute("eventos", eventos);
        return "admin/eventos";
    }

    @GetMapping("/eventos/nuevo")
    public String mostrarFormularioEvento(Model model) {
        model.addAttribute("evento", new Evento());
        return "admin/evento-form";
    }

    @PostMapping("/eventos")
    public String guardarEvento(@Valid @ModelAttribute Evento evento,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/evento-form";
        }
        eventoService.crearEvento(evento);
        redirectAttributes.addFlashAttribute("mensaje", "Evento guardado correctamente");
        return "redirect:/admin/eventos";
    }

    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        List<Pago> pagos = pagoService.listarPagosPorEstado(Pago.EstadoPago.PENDIENTE);
        model.addAttribute("pagos", pagos);
        return "admin/pagos";
    }

    @PostMapping("/pagos/{id}/aprobar")
    public String aprobarPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pagoService.cambiarEstadoPago(id, Pago.EstadoPago.COMPLETADO);
        redirectAttributes.addFlashAttribute("mensaje", "Pago aprobado correctamente");
        return "redirect:/admin/pagos";
    }

    @PostMapping("/pagos/{id}/rechazar")
    public String rechazarPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pagoService.cambiarEstadoPago(id, Pago.EstadoPago.CANCELADO);
        redirectAttributes.addFlashAttribute("mensaje", "Pago rechazado correctamente");
        return "redirect:/admin/pagos";
    }

    // --- Gestión de Usuarios ---
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        // Podríamos añadir una lista de roles si el admin pudiera crear otros admins/instructores
        return "admin/usuario-form"; 
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/usuario-form";
        }
        try {
            usuarioService.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/dashboard"; // O a una lista de usuarios
    }

    // --- Gestión de Clases ---
    @GetMapping("/clases/nuevo")
    public String mostrarFormularioNuevaClase(Model model) {
        model.addAttribute("clase", new Clase());
        List<Usuario> instructores = usuarioService.listarInstructores();
        model.addAttribute("instructores", instructores);
        return "admin/clase-form";
    }

    @PostMapping("/clases/guardar")
    public String guardarClase(@Valid @ModelAttribute("clase") Clase clase,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            // Si hay errores, debemos volver a cargar la lista de instructores
            List<Usuario> instructores = usuarioService.listarInstructores();
            model.addAttribute("instructores", instructores);
            return "admin/clase-form";
        }
        claseService.crearClase(clase);
        redirectAttributes.addFlashAttribute("mensaje", "Clase creada exitosamente.");
        return "redirect:/dashboard"; // O a una lista de clases
    }
}

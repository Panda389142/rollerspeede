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

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private GaleriaItemService galeriaItemService;

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
        pagoService.buscarPorId(id).ifPresent(pago -> {
            pagoService.cambiarEstadoPago(id, Pago.EstadoPago.CANCELADO);
            String mensaje = String.format("Tu pago de S/%.2f para la clase '%s' fue rechazado.", 
                                           pago.getMonto(), 
                                           pago.getClase() != null ? pago.getClase().getNombre() : "N/A");
            notificacionService.crearNotificacion(pago.getUsuario(), mensaje, "/historial-pagos");
            redirectAttributes.addFlashAttribute("mensaje", "Pago rechazado y usuario notificado.");
        });
        return "redirect:/admin/pagos";
    }

    // --- Gestión de Usuarios ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

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

    // --- Gestión de Instructores ---
    @GetMapping("/instructores")
    public String listarInstructores(Model model) {
        List<Usuario> instructores = usuarioService.listarInstructores();
        model.addAttribute("instructores", instructores);
        return "admin/instructores";
    }

    @GetMapping("/instructores/nuevo")
    public String mostrarFormularioNuevoInstructor(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("esInstructor", true); // Para diferenciar en el formulario si es necesario
        return "admin/instructor-form";
    }

    @PostMapping("/instructores/guardar")
    public String guardarInstructor(@Valid @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/instructor-form";
        }
        try {
            // Asegurarse de que el rol sea INSTRUCTOR
            usuario.setRol(Usuario.Rol.INSTRUCTOR);
            usuarioService.crearInstructor(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Instructor creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el instructor: " + e.getMessage());
        }
        return "redirect:/admin/instructores";
    }

    @GetMapping("/instructores/editar/{id}")
    public String mostrarFormularioEditarInstructor(@PathVariable Long id, Model model) {
        usuarioService.buscarPorId(id).ifPresent(usuario -> model.addAttribute("usuario", usuario));
        model.addAttribute("esInstructor", true);
        return "admin/instructor-form";
    }

    @PostMapping("/instructores/actualizar")
    public String actualizarInstructor(@Valid @ModelAttribute("usuario") Usuario usuario,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/instructor-form";
        }
        try {
            // Asegurarse de que el rol no cambie si no se desea, o forzar a INSTRUCTOR
            Usuario existingUser = usuarioService.buscarPorId(usuario.getId())
                                                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
            existingUser.setNombre(usuario.getNombre());
            existingUser.setEmail(usuario.getEmail());
            existingUser.setFechaNacimiento(usuario.getFechaNacimiento());
            existingUser.setGenero(usuario.getGenero());
            existingUser.setTelefono(usuario.getTelefono());
            existingUser.setMedioPago(usuario.getMedioPago());
            // No actualizar la contraseña desde aquí a menos que se provea una nueva
            // No actualizar el rol desde aquí, se mantiene el existente
            usuarioService.actualizarUsuario(existingUser);
            redirectAttributes.addFlashAttribute("mensaje", "Instructor actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el instructor: " + e.getMessage());
        }
        return "redirect:/admin/instructores";
    }

    @PostMapping("/instructores/desactivar/{id}")
    public String desactivarInstructor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.desactivarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor desactivado correctamente.");
        return "redirect:/admin/instructores";
    }

    @PostMapping("/instructores/activar/{id}")
    public String activarInstructor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.activarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor activado correctamente.");
        return "redirect:/admin/instructores";
    }

    // --- Gestión de Clases ---
    @GetMapping("/clases")
    public String listarClases(Model model) {
        List<Clase> clases = claseService.listarTodasLasClases();
        model.addAttribute("clases", clases);
        return "admin/clases";
    }

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
        return "redirect:/admin/clases"; // Changed redirect to /admin/clases
    }

    @GetMapping("/clases/editar/{id}")
    public String mostrarFormularioEditarClase(@PathVariable Long id, Model model) {
        claseService.buscarPorId(id).ifPresent(clase -> model.addAttribute("clase", clase));
        List<Usuario> instructores = usuarioService.listarInstructores();
        model.addAttribute("instructores", instructores);
        return "admin/clase-form";
    }

    @PostMapping("/clases/actualizar")
    public String actualizarClase(@Valid @ModelAttribute("clase") Clase clase,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            List<Usuario> instructores = usuarioService.listarInstructores();
            model.addAttribute("instructores", instructores);
            return "admin/clase-form";
        }
        claseService.actualizarClase(clase);
        redirectAttributes.addFlashAttribute("mensaje", "Clase actualizada exitosamente.");
        return "redirect:/admin/clases";
    }

    @PostMapping("/clases/eliminar/{id}")
    public String eliminarClase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        claseService.desactivarClase(id);
        redirectAttributes.addFlashAttribute("mensaje", "Clase desactivada correctamente.");
        return "redirect:/admin/clases";
    }

    @PostMapping("/clases/activar/{id}")
    public String activarClase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        claseService.activarClase(id);
        redirectAttributes.addFlashAttribute("mensaje", "Clase activada correctamente.");
        return "redirect:/admin/clases";
    }

    @GetMapping("/reportes")
    public String listarReportes(Model model) {
        return "admin/reportes";
    }

    // --- Gestión de Galería ---
    @GetMapping("/galeria")
    public String listarGaleria(Model model) {
        List<GaleriaItem> items = galeriaItemService.findAll();
        model.addAttribute("items", items);
        return "admin/galeria";
    }

    @GetMapping("/galeria/nuevo")
    public String mostrarFormularioGaleria(Model model) {
        model.addAttribute("item", new GaleriaItem());
        return "admin/galeria-form";
    }

    @PostMapping("/galeria/guardar")
    public String guardarGaleriaItem(@Valid @ModelAttribute("item") GaleriaItem item,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/galeria-form";
        }
        galeriaItemService.save(item);
        redirectAttributes.addFlashAttribute("mensaje", "Elemento de galería guardado exitosamente.");
        return "redirect:/admin/galeria";
    }

    @GetMapping("/galeria/editar/{id}")
    public String mostrarFormularioEditarGaleriaItem(@PathVariable Long id, Model model) {
        galeriaItemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        return "admin/galeria-form";
    }

    @PostMapping("/galeria/eliminar/{id}")
    public String eliminarGaleriaItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        galeriaItemService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Elemento de galería eliminado correctamente.");
        return "redirect:/admin/galeria";
    }

}

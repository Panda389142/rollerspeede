package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.service.ClaseService;
import com.rollerspeed.rollerspeed.service.InscripcionService;
import com.rollerspeed.rollerspeed.service.PagoService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/inscripcion")
@PreAuthorize("hasRole('ALUMNO')")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private PagoService pagoService;

    @GetMapping("/inscribir/{claseId}")
    public String mostrarFormularioPago(@PathVariable Long claseId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Clase clase = claseService.buscarPorId(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        // Verificar si ya está inscrito
        if (inscripcionService.estaInscrito(claseId, usuario)) {
            redirectAttributes.addFlashAttribute("error", "Ya estás inscrito en esta clase.");
            return "redirect:/servicios";
        }

        // Verificar si hay cupo
        if (!clase.tieneCupo()) {
            redirectAttributes.addFlashAttribute("error", "Lo sentimos, esta clase ya no tiene cupos disponibles.");
            return "redirect:/servicios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("clase", clase);
        return "inscripcion-pago";
    }

    @PostMapping("/pagar")
    public String procesarPago(@RequestParam Long claseId, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Inscribir al usuario en la clase (esto ya genera el pago)
            boolean exitoInscripcion = inscripcionService.inscribirUsuarioEnClase(claseId, usuario);

            if (exitoInscripcion) {
                redirectAttributes.addFlashAttribute("mensaje", "¡Inscripción y pago exitosos! Bienvenido a la clase.");
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Hubo un error al procesar tu inscripción. Es posible que ya estés inscrito o no haya cupo.");
                return "redirect:/servicios";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/servicios";
        }
    }

    @PostMapping("/cancelar/{claseId}")
    public String cancelarInscripcion(@PathVariable Long claseId, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean exito = inscripcionService.cancelarInscripcionEnClase(claseId, usuario);
        if (exito) {
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción cancelada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la inscripción.");
        }
        return "redirect:/dashboard";
    }
}

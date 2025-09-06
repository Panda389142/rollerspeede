package com.rollerspeed.rollerspeed.controller;

import com.rollerspeed.rollerspeed.service.InscripcionService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/clase/{claseId}")
    public ResponseEntity<String> inscribirAlumno(@PathVariable Long claseId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }
        String email = principal.getName();

        return usuarioService.buscarPorEmail(email)
                .map(usuario -> {
                    boolean exito = inscripcionService.inscribirAlumnoEnClase(claseId, usuario);
                    return exito ? ResponseEntity.ok("success") : ResponseEntity.badRequest().body("error");
                })
                .orElse(ResponseEntity.status(404).body("user_not_found"));
    }

    @PostMapping("/cancelar/{claseId}")
    public ResponseEntity<String> cancelarInscripcion(@PathVariable Long claseId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }
        String email = principal.getName();

        return usuarioService.buscarPorEmail(email)
                .map(usuario -> {
                    boolean exito = inscripcionService.cancelarInscripcionEnClase(claseId, usuario);
                    return exito ? ResponseEntity.ok("success") : ResponseEntity.badRequest().body("error");
                })
                .orElse(ResponseEntity.status(404).body("user_not_found"));
    }
}

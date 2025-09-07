package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.repository.ClaseRepository;
import com.rollerspeed.rollerspeed.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class InscripcionService {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Transactional
    public boolean inscribirUsuarioEnClase(Long claseId, Usuario usuario) {
        Clase clase = claseRepository.findById(claseId).orElse(null);
        if (clase == null || !clase.getActiva()) {
            return false;
        }
        // Verificar si la clase tiene cupo
        if (clase.getAlumnos().size() >= clase.getCapacidadMaxima()) {
            return false;
        }
        // Verificar si el usuario ya está inscrito
        if (clase.getAlumnos().contains(usuario)) {
            return false;
        }
        // Agregar usuario a la clase
        clase.getAlumnos().add(usuario);
        usuario.getClases().add(clase); // Sincronizar el otro lado de la relación
        generarPagoPorInscripcion(usuario, clase);
        claseRepository.save(clase);
        return true;
    }

    private void generarPagoPorInscripcion(Usuario usuario, Clase clase) {
        // Solo generar un pago si la clase tiene un precio y es mayor a 0.
        if (clase.getPrecio() != null && clase.getPrecio() > 0) {
            Pago nuevoPago = new Pago();
            nuevoPago.setUsuario(usuario);
            nuevoPago.setClase(clase);
            nuevoPago.setMonto(BigDecimal.valueOf(clase.getPrecio()));
            nuevoPago.setDescripcion("Inscripción a clase: " + clase.getNombre());
            nuevoPago.setFechaGeneracion(LocalDate.now());
            nuevoPago.setEstado(Pago.EstadoPago.COMPLETADO); // Pago exitoso
            nuevoPago.setFechaPago(LocalDateTime.now());
            nuevoPago.setMedioPago(usuario.getMedioPago());
            pagoRepository.save(nuevoPago);
        }
    }

    @Transactional
    public boolean cancelarInscripcionEnClase(Long claseId, Usuario usuario) {
        Clase clase = claseRepository.findById(claseId).orElse(null);
        if (clase == null) {
            return false;
        }
        // Verificar si el usuario está inscrito en la clase
        if (!clase.getAlumnos().contains(usuario)) {
            return false;
        }
        // Remover usuario de la clase
        clase.getAlumnos().remove(usuario);
        usuario.getClases().remove(clase); // Sincronizar el otro lado de la relación
        claseRepository.save(clase);
        return true;
    }

    public boolean estaInscrito(Long claseId, Usuario usuario) {
        Clase clase = claseRepository.findById(claseId).orElse(null);
        if (clase == null) {
            return false;
        }
        return clase.getAlumnos().contains(usuario);
    }
}
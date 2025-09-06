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

@Service
public class InscripcionService {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Transactional
    public boolean inscribirAlumnoEnClase(Long claseId, Usuario usuario) {
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

    private void generarPagoPorInscripcion(Usuario alumno, Clase clase) {
        Pago nuevoPago = new Pago();
        nuevoPago.setAlumno(alumno);
        nuevoPago.setClase(clase);
        // Asumimos que la clase tiene un precio. Si no, puedes poner un valor por defecto.
        nuevoPago.setMonto(clase.getPrecio() != null ? BigDecimal.valueOf(clase.getPrecio()) : BigDecimal.ZERO);
        nuevoPago.setFechaGeneracion(LocalDate.now());
        nuevoPago.setEstado(Pago.EstadoPago.PENDIENTE);
        pagoRepository.save(nuevoPago);
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
}

package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public Pago registrarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public Pago actualizarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> listarPagosPorAlumno(Usuario alumno) {
        return pagoRepository.findByAlumno(alumno);
    }

    public List<Pago> listarPagosPorEstado(Pago.EstadoPago estado) {
        return pagoRepository.findByEstado(estado);
    }

    public BigDecimal calcularTotalPagosUsuario(Usuario alumno) {
        List<Pago> pagos = pagoRepository.findByAlumno(alumno);
        return pagos.stream()
                .filter(p -> p.getEstado() == Pago.EstadoPago.COMPLETADO)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cambiarEstadoPago(Long id, Pago.EstadoPago estado) {
        pagoRepository.findById(id).ifPresent(pago -> {
            pago.setEstado(estado);
            pagoRepository.save(pago);
        });
    }
}

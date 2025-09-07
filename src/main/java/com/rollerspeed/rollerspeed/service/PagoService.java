package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Clase;
import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository pagoRepository;

    public Pago registrarPago(Pago pago) {
        logger.info("Registrando pago: {}", pago);
        return pagoRepository.save(pago);
    }

    public Pago actualizarPago(Pago pago) {
        logger.info("Actualizando pago: {}", pago);
        return pagoRepository.save(pago);
    }

    public Optional<Pago> buscarPorId(Long id) {
        logger.info("Buscando pago por ID: {}", id);
        return pagoRepository.findById(id);
    }

    public List<Pago> listarPagosPorUsuario(Usuario usuario) {
        logger.info("Listando pagos para el usuario: {}", usuario.getEmail());
        List<Pago> pagos = pagoRepository.findByUsuario(usuario);
        logger.info("Se encontraron {} pagos para el usuario {}", pagos.size(), usuario.getEmail());
        return pagos;
    }

    public List<Pago> listarPagosPorEstado(Pago.EstadoPago estado) {
        logger.info("Listando pagos por estado: {}", estado);
        return pagoRepository.findByEstado(estado);
    }

    public BigDecimal calcularTotalPagosUsuario(Usuario usuario) {
        logger.info("Calculando total de pagos para el usuario: {}", usuario.getEmail());
        List<Pago> pagos = pagoRepository.findByUsuario(usuario);
        return pagos.stream()
                .filter(p -> p.getEstado() == Pago.EstadoPago.COMPLETADO)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cambiarEstadoPago(Long id, Pago.EstadoPago estado) {
        logger.info("Cambiando estado del pago {} a {}", id, estado);
        pagoRepository.findById(id).ifPresent(pago -> {
            pago.setEstado(estado);
            pagoRepository.save(pago);
        });
    }

    public void crearPagoParaClase(Usuario usuario, Clase clase) {
        logger.info("Creando pago para la clase {} y el usuario {}", clase.getNombre(), usuario.getEmail());
        Pago nuevoPago = new Pago();
        nuevoPago.setUsuario(usuario);
        nuevoPago.setClase(clase);
        nuevoPago.setDescripcion("Inscripción a clase: " + clase.getNombre());
        nuevoPago.setMonto(BigDecimal.valueOf(clase.getPrecio()));
        nuevoPago.setEstado(Pago.EstadoPago.COMPLETADO); // Simulación de pago exitoso
        nuevoPago.setMedioPago(usuario.getMedioPago());
        pagoRepository.save(nuevoPago);
        logger.info("Pago creado con ID: {}", nuevoPago.getId());
    }
}
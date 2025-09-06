package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Testimonio;
import com.rollerspeed.rollerspeed.repository.TestimonioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestimonioService {

    @Autowired
    private TestimonioRepository testimonioRepository;

    public Testimonio crearTestimonio(Testimonio testimonio) {
        return testimonioRepository.save(testimonio);
    }

    public Testimonio actualizarTestimonio(Testimonio testimonio) {
        return testimonioRepository.save(testimonio);
    }

    public Optional<Testimonio> buscarPorId(Long id) {
        return testimonioRepository.findById(id);
    }

    public List<Testimonio> listarTestimoniosActivos() {
        return testimonioRepository.findByActivoTrueOrderByFechaCreacionDesc();
    }

    public void desactivarTestimonio(Long id) {
        testimonioRepository.findById(id).ifPresent(testimonio -> {
            testimonio.setActivo(false);
            testimonioRepository.save(testimonio);
        });
    }

    public void activarTestimonio(Long id) {
        testimonioRepository.findById(id).ifPresent(testimonio -> {
            testimonio.setActivo(true);
            testimonioRepository.save(testimonio);
        });
    }
}

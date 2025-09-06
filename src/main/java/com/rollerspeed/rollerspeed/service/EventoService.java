package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Evento;
import com.rollerspeed.rollerspeed.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public List<Evento> listarEventosActivos() {
        return eventoRepository.findByActivoTrueOrderByFechaEventoAsc();
    }

    public List<Evento> listarEventosFuturos() {
        return eventoRepository.findByFechaEventoAfterAndActivoTrueOrderByFechaEventoAsc(LocalDateTime.now());
    }

    public void desactivarEvento(Long id) {
        eventoRepository.findById(id).ifPresent(evento -> {
            evento.setActivo(false);
            eventoRepository.save(evento);
        });
    }

    public void activarEvento(Long id) {
        eventoRepository.findById(id).ifPresent(evento -> {
            evento.setActivo(true);
            eventoRepository.save(evento);
        });
    }
}

package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Noticia;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    public Noticia crearNoticia(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    public Noticia actualizarNoticia(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    public Optional<Noticia> buscarPorId(Long id) {
        return noticiaRepository.findById(id);
    }

    public List<Noticia> listarNoticiasActivas() {
        return noticiaRepository.findByActivoTrueOrderByFechaPublicacionDesc();
    }

    public List<Noticia> listarNoticiasPorAutor(Usuario autor) {
        return noticiaRepository.findByAutorAndActivoTrueOrderByFechaPublicacionDesc(autor);
    }

    public void desactivarNoticia(Long id) {
        noticiaRepository.findById(id).ifPresent(noticia -> {
            noticia.setActivo(false);
            noticiaRepository.save(noticia);
        });
    }

    public void activarNoticia(Long id) {
        noticiaRepository.findById(id).ifPresent(noticia -> {
            noticia.setActivo(true);
            noticiaRepository.save(noticia);
        });
    }
}

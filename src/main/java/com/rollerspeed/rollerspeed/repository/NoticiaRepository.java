package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Noticia;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findByActivoTrueOrderByFechaPublicacionDesc();

    List<Noticia> findByAutorAndActivoTrueOrderByFechaPublicacionDesc(Usuario autor);
}

package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Notificacion;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioAndLeidoOrderByFechaCreacionDesc(Usuario usuario, boolean leido);
    long countByUsuarioAndLeido(Usuario usuario, boolean leido);
}

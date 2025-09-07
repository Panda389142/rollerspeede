package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Notificacion;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public void crearNotificacion(Usuario usuario, String mensaje, String url) {
        Notificacion notificacion = new Notificacion(usuario, mensaje, url);
        notificacionRepository.save(notificacion);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarNotificacionesNoLeidas(Usuario usuario) {
        return notificacionRepository.findByUsuarioAndLeidoOrderByFechaCreacionDesc(usuario, false);
    }

    @Transactional(readOnly = true)
    public long contarNotificacionesNoLeidas(Usuario usuario) {
        return notificacionRepository.countByUsuarioAndLeido(usuario, false);
    }

    public void marcarComoLeida(Long notificacionId) {
        notificacionRepository.findById(notificacionId).ifPresent(notificacion -> {
            notificacion.setLeido(true);
            notificacionRepository.save(notificacion);
        });
    }

    public void marcarTodasComoLeidas(Usuario usuario) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioAndLeidoOrderByFechaCreacionDesc(usuario, false);
        noLeidas.forEach(notificacion -> notificacion.setLeido(true));
        notificacionRepository.saveAll(noLeidas);
    }
}

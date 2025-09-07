package com.rollerspeed.rollerspeed.config;

import com.rollerspeed.rollerspeed.model.Notificacion;
import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.service.NotificacionService;
import com.rollerspeed.rollerspeed.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("unreadNotificationsCount")
    public long getUnreadNotificationsCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            return usuarioService.buscarPorEmail(email)
                    .map(notificacionService::contarNotificacionesNoLeidas)
                    .orElse(0L);
        }
        return 0L;
    }

    @ModelAttribute("unreadNotifications")
    public List<Notificacion> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            return usuarioService.buscarPorEmail(email)
                    .map(notificacionService::listarNotificacionesNoLeidas)
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}

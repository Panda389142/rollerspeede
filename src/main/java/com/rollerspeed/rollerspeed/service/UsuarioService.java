package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.Usuario;
import com.rollerspeed.rollerspeed.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        try {
            // Verificar si el email ya existe
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new RuntimeException("Ya existe un usuario con este email");
            }

            // Validaciones adicionales
            if (usuario.getFechaNacimiento() == null) {
                throw new RuntimeException("La fecha de nacimiento es obligatoria");
            }
            if (usuario.getMedioPago() == null) {
                throw new RuntimeException("El método de pago es obligatorio");
            }

            // Codificar la contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            
            // Establecer valores por defecto
            usuario.setRol(Usuario.Rol.ALUMNO);
            usuario.setActivo(true);
            usuario.setFechaRegistro(LocalDateTime.now());

            // Guardar el usuario
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // Log para depuración
            System.out.println("Usuario guardado en la base de datos: " + usuarioGuardado.getId());
            
            return usuarioGuardado;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el usuario: " + e.getMessage(), e);
        }
    }

    public Usuario crearInstructor(Usuario instructor) {
        if (usuarioRepository.existsByEmail(instructor.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email");
        }
        
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
        instructor.setRol(Usuario.Rol.INSTRUCTOR);
        instructor.setActivo(true);
        
        return usuarioRepository.save(instructor);
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAlumnos() {
        return usuarioRepository.findActiveUsersByRol(Usuario.Rol.ALUMNO);
    }

    public List<Usuario> listarInstructores() {
        return usuarioRepository.findActiveUsersByRol(Usuario.Rol.INSTRUCTOR);
    }

    public void desactivarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });
    }

    public void activarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        });
    }

    public long contarAlumnos() {
        return usuarioRepository.countByRolAndActivoTrue(Usuario.Rol.ALUMNO);
    }

    public long contarInstructores() {
        return usuarioRepository.countByRolAndActivoTrue(Usuario.Rol.INSTRUCTOR);
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public boolean cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
                return true;
            }
        }
        
        return false;
    }
}
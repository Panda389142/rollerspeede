package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByRol(Usuario.Rol rol);

    List<Usuario> findByRol(Usuario.Rol rol);

    List<Usuario> findByRolAndActivoTrue(Usuario.Rol rol);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true ORDER BY u.nombre")
    List<Usuario> findActiveUsersByRol(@Param("rol") Usuario.Rol rol);

    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.activo = true")
    List<Usuario> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    long countByRolAndActivoTrue(@Param("rol") Usuario.Rol rol);
}
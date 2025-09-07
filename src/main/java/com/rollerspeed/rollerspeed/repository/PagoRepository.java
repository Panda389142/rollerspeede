package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Query("SELECT p FROM Pago p LEFT JOIN FETCH p.clase WHERE p.usuario = :usuario ORDER BY p.fechaGeneracion DESC")
    List<Pago> findByUsuario(@Param("usuario") Usuario usuario);

    List<Pago> findByEstado(Pago.EstadoPago estado);

}
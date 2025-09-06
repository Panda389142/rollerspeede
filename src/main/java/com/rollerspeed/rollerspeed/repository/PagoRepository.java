package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Pago;
import com.rollerspeed.rollerspeed.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByAlumno(Usuario alumno);

    List<Pago> findByEstado(Pago.EstadoPago estado);

}
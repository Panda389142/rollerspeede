package com.rollerspeed.rollerspeed.repository;

import com.rollerspeed.rollerspeed.model.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Long> {

    List<Testimonio> findByActivoTrueOrderByFechaCreacionDesc();
}

package com.pruebafinanciera.backend.repository;

import com.pruebafinanciera.backend.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository
        extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}
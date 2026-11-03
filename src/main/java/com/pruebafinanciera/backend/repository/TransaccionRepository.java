package com.pruebafinanciera.backend.repository;

import com.pruebafinanciera.backend.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository
        extends JpaRepository<Transaccion, Long> {
}
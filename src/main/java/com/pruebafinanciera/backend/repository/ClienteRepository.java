package com.pruebafinanciera.backend.repository;

import com.pruebafinanciera.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
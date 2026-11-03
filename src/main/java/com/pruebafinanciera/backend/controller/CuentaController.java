package com.pruebafinanciera.backend.controller;

import com.pruebafinanciera.backend.dto.CrearCuentaRequest;
import com.pruebafinanciera.backend.entity.Cuenta;
import com.pruebafinanciera.backend.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(
            @Valid @RequestBody CrearCuentaRequest request) {

        Cuenta nuevaCuenta = cuentaService.crearCuenta(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaCuenta);
    }

    @GetMapping
    public ResponseEntity<List<Cuenta>> listarCuentas() {

        return ResponseEntity.ok(
                cuentaService.listarCuentas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> buscarCuentaPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaService.buscarCuentaPorId(id)
        );
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Cuenta> activarCuenta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaService.activarCuenta(id)
        );
    }

    @PatchMapping("/{id}/inactivar")
    public ResponseEntity<Cuenta> inactivarCuenta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaService.inactivarCuenta(id)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Cuenta> cancelarCuenta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cuentaService.cancelarCuenta(id)
        );
    }
}
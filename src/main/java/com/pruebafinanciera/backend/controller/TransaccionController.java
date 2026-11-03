package com.pruebafinanciera.backend.controller;

import com.pruebafinanciera.backend.dto.ConsignacionRequest;
import com.pruebafinanciera.backend.dto.RetiroRequest;
import com.pruebafinanciera.backend.dto.TransferenciaRequest;
import com.pruebafinanciera.backend.entity.Movimiento;
import com.pruebafinanciera.backend.entity.Transaccion;
import com.pruebafinanciera.backend.service.TransaccionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/consignacion")
    public ResponseEntity<Transaccion> consignar(
            @Valid @RequestBody ConsignacionRequest request) {

        return ResponseEntity.ok(
                transaccionService.consignar(request)
        );
    }

    @PostMapping("/retiro")
    public ResponseEntity<Transaccion> retirar(
            @Valid @RequestBody RetiroRequest request) {

        return ResponseEntity.ok(
                transaccionService.retirar(request)
        );
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Transaccion> transferir(
            @Valid @RequestBody TransferenciaRequest request) {

        return ResponseEntity.ok(
                transaccionService.transferir(request)
        );
    }

    @GetMapping("/cuenta/{cuentaId}/movimientos")
    public ResponseEntity<List<Movimiento>> consultarMovimientos(
            @PathVariable Long cuentaId) {

        return ResponseEntity.ok(
                transaccionService.consultarMovimientos(cuentaId)
        );
    }
}
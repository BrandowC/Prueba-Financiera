package com.pruebafinanciera.backend.controller;

import com.pruebafinanciera.backend.entity.Cliente;
import com.pruebafinanciera.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(
            @Valid @RequestBody Cliente cliente) {

        Cliente nuevoCliente = clienteService.crearCliente(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoCliente);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {

        return ResponseEntity.ok(
                clienteService.listarClientes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                clienteService.buscarClientePorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        return ResponseEntity.ok(
                clienteService.actualizarCliente(id, cliente)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(
            @PathVariable Long id) {

        clienteService.eliminarCliente(id);

        return ResponseEntity.noContent().build();
    }
}
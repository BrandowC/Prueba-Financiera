package com.pruebafinanciera.backend.controller;

import com.pruebafinanciera.backend.entity.Cliente;
import com.pruebafinanciera.backend.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    private ClienteController clienteController;

    private Cliente cliente;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        clienteController =
                new ClienteController(clienteService);

        cliente = new Cliente();

        cliente.setId(1L);
        cliente.setTipoIdentificacion("CC");
        cliente.setNumeroIdentificacion("123456789");
        cliente.setNombres("Carlos");
        cliente.setApellido("Ramirez");
        cliente.setCorreoElectronico("carlos@gmail.com");
        cliente.setFechaNacimiento(
                LocalDate.now().minusYears(25)
        );
    }

    @Test
    void debeCrearCliente() {

        when(clienteService.crearCliente(any(Cliente.class)))
                .thenReturn(cliente);

        ResponseEntity<Cliente> respuesta =
                clienteController.crearCliente(cliente);

        assertEquals(
                HttpStatus.CREATED,
                respuesta.getStatusCode()
        );

        assertNotNull(respuesta.getBody());

        assertEquals(
                "Carlos",
                respuesta.getBody().getNombres()
        );

        verify(clienteService, times(1))
                .crearCliente(cliente);
    }

    @Test
    void debeListarClientes() {

        when(clienteService.listarClientes())
                .thenReturn(List.of(cliente));

        ResponseEntity<List<Cliente>> respuesta =
                clienteController.listarClientes();

        assertEquals(
                HttpStatus.OK,
                respuesta.getStatusCode()
        );

        assertNotNull(respuesta.getBody());

        assertEquals(
                1,
                respuesta.getBody().size()
        );

        verify(clienteService, times(1))
                .listarClientes();
    }
}
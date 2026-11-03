package com.pruebafinanciera.backend.service;

import com.pruebafinanciera.backend.entity.Cliente;
import com.pruebafinanciera.backend.repository.ClienteRepository;
import com.pruebafinanciera.backend.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {

        cliente = new Cliente();

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
    void debeCrearClienteMayorDeEdad() {

        when(clienteRepository.save(cliente))
                .thenReturn(cliente);

        Cliente resultado =
                clienteService.crearCliente(cliente);

        assertNotNull(resultado);

        verify(clienteRepository, times(1))
                .save(cliente);
    }

    @Test
    void noDebeCrearClienteMenorDeEdad() {

        cliente.setFechaNacimiento(
                LocalDate.now().minusYears(15)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService.crearCliente(cliente)
                );

        assertEquals(
                "El cliente debe ser mayor de edad",
                exception.getMessage()
        );

        verify(clienteRepository, never())
                .save(any());
    }

    @Test
    void noDebeEliminarClienteConCuentas() {

        Long clienteId = 1L;

        when(clienteRepository.findById(clienteId))
                .thenReturn(java.util.Optional.of(cliente));

        when(cuentaRepository.existsByClienteId(clienteId))
                .thenReturn(true);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> clienteService.eliminarCliente(clienteId)
                );

        assertEquals(
                "No se puede eliminar el cliente porque tiene productos vinculados",
                exception.getMessage()
        );

        verify(clienteRepository, never())
                .delete(any());
    }
}
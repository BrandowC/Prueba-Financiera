package com.pruebafinanciera.backend.service;

import com.pruebafinanciera.backend.entity.Cliente;
import com.pruebafinanciera.backend.repository.ClienteRepository;
import com.pruebafinanciera.backend.repository.CuentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            CuentaRepository cuentaRepository) {

        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public Cliente crearCliente(Cliente cliente) {

        validarMayorEdad(cliente.getFechaNacimiento());

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado"));
    }

    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {

        Cliente clienteExistente = buscarClientePorId(id);

        validarMayorEdad(clienteActualizado.getFechaNacimiento());

        clienteExistente.setTipoIdentificacion(
                clienteActualizado.getTipoIdentificacion());

        clienteExistente.setNumeroIdentificacion(
                clienteActualizado.getNumeroIdentificacion());

        clienteExistente.setNombres(
                clienteActualizado.getNombres());

        clienteExistente.setApellido(
                clienteActualizado.getApellido());

        clienteExistente.setCorreoElectronico(
                clienteActualizado.getCorreoElectronico());

        clienteExistente.setFechaNacimiento(
                clienteActualizado.getFechaNacimiento());

        return clienteRepository.save(clienteExistente);
    }

    public void eliminarCliente(Long id) {

        Cliente cliente = buscarClientePorId(id);

        if (cuentaRepository.existsByClienteId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene productos vinculados"
            );
        }

        clienteRepository.delete(cliente);
    }

    private void validarMayorEdad(LocalDate fechaNacimiento) {

        if (fechaNacimiento == null) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento es obligatoria");
        }

        int edad = Period.between(
                fechaNacimiento,
                LocalDate.now()
        ).getYears();

        if (edad < 18) {
            throw new IllegalArgumentException(
                    "El cliente debe ser mayor de edad");
        }
    }
}
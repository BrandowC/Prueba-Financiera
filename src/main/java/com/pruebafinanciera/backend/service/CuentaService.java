package com.pruebafinanciera.backend.service;

import com.pruebafinanciera.backend.dto.CrearCuentaRequest;
import com.pruebafinanciera.backend.entity.Cliente;
import com.pruebafinanciera.backend.entity.Cuenta;
import com.pruebafinanciera.backend.enums.EstadoCuenta;
import com.pruebafinanciera.backend.enums.TipoCuenta;
import com.pruebafinanciera.backend.repository.ClienteRepository;
import com.pruebafinanciera.backend.repository.CuentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    public CuentaService(
            CuentaRepository cuentaRepository,
            ClienteRepository clienteRepository) {

        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    public Cuenta crearCuenta(CrearCuentaRequest request) {

        Cliente cliente = clienteRepository
                .findById(request.getClienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado"));

        Cuenta cuenta = new Cuenta();

        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setNumeroCuenta(
                generarNumeroCuenta(request.getTipoCuenta())
        );

        cuenta.setEstado(EstadoCuenta.ACTIVA);

        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setSaldoDisponible(BigDecimal.ZERO);

        cuenta.setExentaGMF(
                request.getExentaGMF() != null
                        ? request.getExentaGMF()
                        : false
        );

        cuenta.setCliente(cliente);

        return cuentaRepository.save(cuenta);
    }

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Cuenta buscarCuentaPorId(Long id) {

        return cuentaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada"));
    }

    public Cuenta buscarCuentaPorNumero(String numeroCuenta) {

        return cuentaRepository
                .findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() ->
                        new RuntimeException("Cuenta no encontrada"));
    }

    public Cuenta activarCuenta(Long id) {

        Cuenta cuenta = buscarCuentaPorId(id);

        cuenta.setEstado(EstadoCuenta.ACTIVA);

        return cuentaRepository.save(cuenta);
    }

    public Cuenta inactivarCuenta(Long id) {

        Cuenta cuenta = buscarCuentaPorId(id);

        cuenta.setEstado(EstadoCuenta.INACTIVA);

        return cuentaRepository.save(cuenta);
    }

    public Cuenta cancelarCuenta(Long id) {

        Cuenta cuenta = buscarCuentaPorId(id);

        if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "Solo se puede cancelar una cuenta con saldo igual a cero"
            );
        }

        cuenta.setEstado(EstadoCuenta.CANCELADA);

        return cuentaRepository.save(cuenta);
    }

    private String generarNumeroCuenta(TipoCuenta tipoCuenta) {

        String prefijo;

        if (tipoCuenta == TipoCuenta.AHORROS) {
            prefijo = "53";
        } else {
            prefijo = "33";
        }

        String numeroCuenta;

        do {

            int numeroAleatorio =
                    ThreadLocalRandom.current()
                            .nextInt(0, 100_000_000);

            String ochoDigitos =
                    String.format("%08d", numeroAleatorio);

            numeroCuenta = prefijo + ochoDigitos;

        } while (
                cuentaRepository.existsByNumeroCuenta(numeroCuenta)
        );

        return numeroCuenta;
    }
}
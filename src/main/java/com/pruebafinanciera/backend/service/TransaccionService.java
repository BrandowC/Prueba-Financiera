package com.pruebafinanciera.backend.service;

import com.pruebafinanciera.backend.dto.ConsignacionRequest;
import com.pruebafinanciera.backend.dto.RetiroRequest;
import com.pruebafinanciera.backend.dto.TransferenciaRequest;
import com.pruebafinanciera.backend.entity.Cuenta;
import com.pruebafinanciera.backend.entity.Movimiento;
import com.pruebafinanciera.backend.entity.Transaccion;
import com.pruebafinanciera.backend.enums.EstadoCuenta;
import com.pruebafinanciera.backend.enums.TipoCuenta;
import com.pruebafinanciera.backend.enums.TipoMovimiento;
import com.pruebafinanciera.backend.enums.TipoTransaccion;
import com.pruebafinanciera.backend.repository.CuentaRepository;
import com.pruebafinanciera.backend.repository.MovimientoRepository;
import com.pruebafinanciera.backend.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransaccionService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final MovimientoRepository movimientoRepository;

    public TransaccionService(
            CuentaRepository cuentaRepository,
            TransaccionRepository transaccionRepository,
            MovimientoRepository movimientoRepository) {

        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public Transaccion consignar(ConsignacionRequest request) {

        Cuenta cuenta = buscarCuenta(request.getNumeroCuenta());

        validarCuentaActiva(cuenta);

        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal saldoNuevo =
                saldoAnterior.add(request.getMonto());

        cuenta.setSaldo(saldoNuevo);
        cuenta.setSaldoDisponible(saldoNuevo);

        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();

        transaccion.setTipo(TipoTransaccion.CONSIGNACION);
        transaccion.setMonto(request.getMonto());
        transaccion.setCuentaDestino(cuenta);

        transaccion = transaccionRepository.save(transaccion);

        crearMovimiento(
                cuenta,
                transaccion,
                TipoMovimiento.CREDITO,
                request.getMonto(),
                saldoAnterior,
                saldoNuevo
        );

        return transaccion;
    }

    @Transactional
    public Transaccion retirar(RetiroRequest request) {

        Cuenta cuenta = buscarCuenta(request.getNumeroCuenta());

        validarCuentaActiva(cuenta);

        BigDecimal saldoAnterior = cuenta.getSaldo();

        if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS
                && saldoAnterior.compareTo(request.getMonto()) < 0) {

            throw new IllegalStateException(
                    "La cuenta de ahorros no puede quedar con saldo negativo"
            );
        }

        BigDecimal saldoNuevo =
                saldoAnterior.subtract(request.getMonto());

        cuenta.setSaldo(saldoNuevo);
        cuenta.setSaldoDisponible(saldoNuevo);

        cuentaRepository.save(cuenta);

        Transaccion transaccion = new Transaccion();

        transaccion.setTipo(TipoTransaccion.RETIRO);
        transaccion.setMonto(request.getMonto());
        transaccion.setCuentaOrigen(cuenta);

        transaccion = transaccionRepository.save(transaccion);

        crearMovimiento(
                cuenta,
                transaccion,
                TipoMovimiento.DEBITO,
                request.getMonto(),
                saldoAnterior,
                saldoNuevo
        );

        return transaccion;
    }

    @Transactional
    public Transaccion transferir(
            TransferenciaRequest request) {

        if (request.getCuentaOrigen()
                .equals(request.getCuentaDestino())) {

            throw new IllegalArgumentException(
                    "La cuenta origen y destino no pueden ser iguales"
            );
        }

        Cuenta origen =
                buscarCuenta(request.getCuentaOrigen());

        Cuenta destino =
                buscarCuenta(request.getCuentaDestino());

        validarCuentaActiva(origen);
        validarCuentaActiva(destino);

        BigDecimal saldoOrigenAnterior =
                origen.getSaldo();

        BigDecimal saldoDestinoAnterior =
                destino.getSaldo();

        if (origen.getTipoCuenta() == TipoCuenta.AHORROS
                && saldoOrigenAnterior
                .compareTo(request.getMonto()) < 0) {

            throw new IllegalStateException(
                    "Saldo insuficiente en la cuenta de ahorros"
            );
        }

        BigDecimal nuevoSaldoOrigen =
                saldoOrigenAnterior.subtract(
                        request.getMonto()
                );

        BigDecimal nuevoSaldoDestino =
                saldoDestinoAnterior.add(
                        request.getMonto()
                );

        origen.setSaldo(nuevoSaldoOrigen);
        origen.setSaldoDisponible(nuevoSaldoOrigen);

        destino.setSaldo(nuevoSaldoDestino);
        destino.setSaldoDisponible(nuevoSaldoDestino);

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        Transaccion transaccion = new Transaccion();

        transaccion.setTipo(
                TipoTransaccion.TRANSFERENCIA
        );

        transaccion.setMonto(request.getMonto());
        transaccion.setCuentaOrigen(origen);
        transaccion.setCuentaDestino(destino);

        transaccion =
                transaccionRepository.save(transaccion);

        crearMovimiento(
                origen,
                transaccion,
                TipoMovimiento.DEBITO,
                request.getMonto(),
                saldoOrigenAnterior,
                nuevoSaldoOrigen
        );

        crearMovimiento(
                destino,
                transaccion,
                TipoMovimiento.CREDITO,
                request.getMonto(),
                saldoDestinoAnterior,
                nuevoSaldoDestino
        );

        return transaccion;
    }

    public List<Movimiento> consultarMovimientos(
            Long cuentaId) {

        return movimientoRepository
                .findByCuentaIdOrderByFechaDesc(
                        cuentaId
                );
    }

    private Cuenta buscarCuenta(String numeroCuenta) {

        return cuentaRepository
                .findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cuenta no encontrada"
                        )
                );
    }

    private void validarCuentaActiva(Cuenta cuenta) {

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {

            throw new IllegalStateException(
                    "La cuenta debe estar activa"
            );
        }
    }

    private void crearMovimiento(
            Cuenta cuenta,
            Transaccion transaccion,
            TipoMovimiento tipo,
            BigDecimal monto,
            BigDecimal saldoAnterior,
            BigDecimal saldoPosterior) {

        Movimiento movimiento = new Movimiento();

        movimiento.setCuenta(cuenta);
        movimiento.setTransaccion(transaccion);
        movimiento.setTipo(tipo);
        movimiento.setMonto(monto);
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(saldoPosterior);

        movimientoRepository.save(movimiento);
    }
}
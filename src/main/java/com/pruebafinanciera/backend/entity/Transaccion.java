package com.pruebafinanciera.backend.entity;

import com.pruebafinanciera.backend.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    private Cuenta cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    private Cuenta cuentaDestino;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}
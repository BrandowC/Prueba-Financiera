package com.pruebafinanciera.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConsignacionRequest {

    @NotBlank
    private String numeroCuenta;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;
}
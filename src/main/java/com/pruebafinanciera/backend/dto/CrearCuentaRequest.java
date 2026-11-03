package com.pruebafinanciera.backend.dto;

import com.pruebafinanciera.backend.enums.TipoCuenta;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearCuentaRequest {

    @NotNull
    private TipoCuenta tipoCuenta;

    @NotNull
    private Long clienteId;

    private Boolean exentaGMF;
}
package com.pruebafinanciera.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String tipoIdentificacion;

    @NotBlank
    @Column(nullable = false)
    private String numeroIdentificacion;

    @NotBlank
    @Size(min = 2)
    @Column(nullable = false)
    private String nombres;

    @NotBlank
    @Size(min = 2)
    @Column(nullable = false)
    private String apellido;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String correoElectronico;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
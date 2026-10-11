package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El dni es obligatorio")
    @Size(min = 8, max = 8, message = "El dni debe contener 8 digitos")
    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 50, message = "Los nombres deben tener entre 2 a 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos tienen entre 2 a 50 caracteres")
    @Column(nullable = false, length = 50)
    private String apellidos;

    @NotBlank(message = "El numero de telefono es obligatorio")
    @Size(min = 9, max = 9, message = "El telefono debe tener 9 digitos")
    @Column(nullable = false, length = 9)
    private String telefono;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean activo;
}

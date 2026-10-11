package com.example.demo.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo de barras es obligatorio")
    @Size(min = 13, max = 13, message = "El codigo de barras debe tener 13 digitos")
    @Column(name = "codigo_barras", nullable = false, unique = true, length = 13)
    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "EL nombre debe tener entre 2 a 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Las unidades de medida son obligatorias")
    @Size(min = 2, max = 20, message = "Las unidades de medida deben tener entre 2 a 20 caracteres")
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "Minimo es s/. 0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean activo;
}

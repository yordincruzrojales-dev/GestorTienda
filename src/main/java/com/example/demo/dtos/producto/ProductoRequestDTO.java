package com.example.demo.dtos.producto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {

    @NotBlank(message = "El codigo de barras es obligatorio")
    @Size(min = 13, max = 13, message = "El codigo de barras debe tener 13 digitos")
    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "EL nombre debe tener entre 2 a 100 caracteres")
    private String nombre;

    @NotBlank(message = "Las unidades de medida son obligatorias")
    @Size(min = 2, max = 20, message = "Las unidades de medida deben tener entre 2 a 20 caracteres")
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mínimo es S/. 0.01")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;
}

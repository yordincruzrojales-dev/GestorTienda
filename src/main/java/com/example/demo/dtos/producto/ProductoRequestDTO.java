package com.example.demo.dtos.producto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO para la creación y actualización de un producto")
public class ProductoRequestDTO {

    @Schema(description = "Código de barras único del producto (13 dígitos EAN-13)", example = "7751234567890")
    @NotBlank(message = "El codigo de barras es obligatorio")
    @Size(min = 13, max = 13, message = "El codigo de barras debe tener 13 digitos")
    private String codigoBarras;

    @Schema(description = "Nombre o descripción del producto", example = "Arroz Extra 1kg")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "EL nombre debe tener entre 2 a 100 caracteres")
    private String nombre;

    @Schema(description = "Unidad de medida del producto", example = "Bolsa")
    @NotBlank(message = "Las unidades de medida son obligatorias")
    @Size(min = 2, max = 20, message = "Las unidades de medida deben tener entre 2 a 20 caracteres")
    private String unidadMedida;

    @Schema(description = "Precio unitario del producto", example = "4.50")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mínimo es S/. 0.01")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precio;

    @Schema(description = "Cantidad de stock disponible", example = "100")
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Estado de actividad del producto", example = "true")
    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;
}
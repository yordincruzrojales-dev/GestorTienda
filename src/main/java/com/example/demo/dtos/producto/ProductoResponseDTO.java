package com.example.demo.dtos.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con la información detallada de un producto")
public class ProductoResponseDTO {

    @Schema(description = "Identificador único del producto", example = "10")
    private Long id;

    @Schema(description = "Código de barras único (EAN-13)", example = "7751234567890")
    private String codigoBarras;

    @Schema(description = "Nombre o descripción del producto", example = "Arroz Extra 1kg")
    private String nombre;

    @Schema(description = "Unidad de medida del producto", example = "Bolsa")
    private String unidadMedida;

    @Schema(description = "Precio unitario del producto", example = "4.50")
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en stock", example = "100")
    private Integer stock;

    @Schema(description = "Estado de actividad del producto", example = "true")
    private Boolean activo;
}
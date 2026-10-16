package com.example.demo.dtos.venta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para un ítem o detalle individual de una venta")
public class DetalleVentaResponseDTO {

    @Schema(description = "Identificador único del detalle de venta", example = "100")
    private Long id;

    @Schema(description = "Identificador único del producto", example = "10")
    private Long idProducto;

    @Schema(description = "Nombre o descripción del producto", example = "Arroz Extra 1kg")
    private String nombreProducto;

    @Schema(description = "Cantidad comprada del producto", example = "3")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto al momento de la venta", example = "4.50")
    private BigDecimal precioUnitario;

    @Schema(description = "Subtotal calculado para este ítem (cantidad * precioUnitario)", example = "13.50")
    private BigDecimal subtotal;
}
package com.example.demo.dtos.venta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO que representa un ítem individual dentro de la solicitud de venta")
public class DetalleVentaRequestDTO {

    @Schema(description = "Identificador único del producto a comprar", example = "10")
    @NotNull(message = "EL producto no puede ser nulo")
    @Positive
    private Long idProducto;

    @Schema(description = "Cantidad solicitada del producto", example = "3")
    @NotNull(message = "Es necesario una cantidad")
    @Positive
    private Integer cantidad;
}
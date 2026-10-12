package com.example.demo.dtos.venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleVentaRequestDTO {

    @NotNull(message = "EL producto no puede ser nulo")
    @Positive
    private Long idProducto;

    @NotNull(message = "Es necesario una cantidad")
    @Positive
    private Integer cantidad;
}

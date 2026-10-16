package com.example.demo.dtos.venta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para la solicitud de registro de una nueva venta")
public class VentaRequestDTO {

    @Schema(description = "Identificador único del cliente que realiza la compra", example = "1")
    @NotNull(message = "El cliente no puede ser nulo")
    @Positive
    private Long idCliente;

    @Schema(description = "Lista con el detalle de productos y cantidades a adquirir")
    @NotNull(message = "La lista no puede ser nula")
    @Size(min = 1, message = "La lista debe contener al menos un producto")
    @Valid
    private List<DetalleVentaRequestDTO> detallesVenta;
}
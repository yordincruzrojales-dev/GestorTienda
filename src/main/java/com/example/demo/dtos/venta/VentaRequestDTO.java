package com.example.demo.dtos.venta;

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
public class VentaRequestDTO {

    @NotNull(message = "El cliente no puede ser nulo")
    @Positive
    private Long idCliente;

    @NotNull(message = "La lista no puede ser nula")
    @Size(min = 1, message = "La lista debe contener al menos un producto")
    @Valid
    private List<DetalleVentaRequestDTO> detallesVenta;
}

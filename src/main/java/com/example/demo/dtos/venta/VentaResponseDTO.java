package com.example.demo.dtos.venta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con el resumen completo del comprobante de venta")
public class VentaResponseDTO {

    @Schema(description = "Identificador único de la venta", example = "50")
    private Long id;

    @Schema(description = "Identificador único del cliente", example = "1")
    private Long idCliente;

    @Schema(description = "DNI del cliente", example = "78945612")
    private String dniCliente;

    @Schema(description = "Nombres completos del cliente", example = "Juan Carlos")
    private String nombresCliente;

    @Schema(description = "Lista detallada de los ítems comprados")
    private List<DetalleVentaResponseDTO> detallesVenta;

    @Schema(description = "Monto total acumulado de la venta", example = "45.00")
    private BigDecimal total;

    @Schema(description = "Fecha y hora en que se registró la venta", example = "2026-10-15T14:30:00")
    private LocalDateTime fechaHora;
}
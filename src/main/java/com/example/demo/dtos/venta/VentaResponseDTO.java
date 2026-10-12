package com.example.demo.dtos.venta;

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
public class VentaResponseDTO {
    private Long id;
    private Long idCliente;
    private String dniCliente;
    private String nombresCliente;
    private List<DetalleVentaResponseDTO> detallesVenta;
    private BigDecimal total;
    private LocalDateTime fechaHora;
}

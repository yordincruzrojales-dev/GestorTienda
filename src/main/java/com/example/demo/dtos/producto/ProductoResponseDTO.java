package com.example.demo.dtos.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    private Long id;
    private String codigoBarras;
    private String nombre;
    private String unidadMedida;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;

}

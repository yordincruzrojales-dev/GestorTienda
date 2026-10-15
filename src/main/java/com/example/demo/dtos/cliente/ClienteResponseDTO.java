package com.example.demo.dtos.cliente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private Boolean activo;
}

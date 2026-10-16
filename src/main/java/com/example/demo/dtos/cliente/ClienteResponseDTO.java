package com.example.demo.dtos.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con la información detallada de un cliente")
public class ClienteResponseDTO {

    @Schema(description = "Identificador único del cliente", example = "1")
    private Long id;

    @Schema(description = "Documento Nacional de Identidad", example = "78945612")
    private String dni;

    @Schema(description = "Nombres del cliente", example = "Juan Carlos")
    private String nombres;

    @Schema(description = "Apellidos del cliente", example = "Pérez Gómez")
    private String apellidos;

    @Schema(description = "Número telefónico de contacto", example = "987654321")
    private String telefono;

    @Schema(description = "Estado actual del cliente en el sistema", example = "true")
    private Boolean activo;
}
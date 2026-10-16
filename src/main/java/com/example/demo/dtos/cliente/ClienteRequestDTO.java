package com.example.demo.dtos.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para la creación y actualización de un cliente")
public class ClienteRequestDTO {

    @Schema(description = "Documento Nacional de Identidad (8 dígitos)", example = "78945612")
    @NotBlank(message = "El dni es obligatorio")
    @Size(min = 8, max = 8, message = "El dni debe contener 8 digitos")
    private String dni;

    @Schema(description = "Nombres del cliente", example = "Juan Carlos")
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 50, message = "Los nombres deben tener entre 2 a 50 caracteres")
    private String nombres;

    @Schema(description = "Apellidos del cliente", example = "Pérez Gómez")
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos tienen entre 2 a 50 caracteres")
    private String apellidos;

    @Schema(description = "Número de teléfono (9 dígitos)", example = "987654321")
    @NotBlank(message = "El numero de telefono es obligatorio")
    @Size(min = 9, max = 9, message = "El telefono debe tener 9 digitos")
    private String telefono;

    @Schema(description = "Estado de actividad del cliente", example = "true")
    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;
}
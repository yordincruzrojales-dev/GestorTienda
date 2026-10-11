package com.example.demo.dtos.cliente;

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
public class ClienteRequestDTO {

    @NotBlank(message = "El dni es obligatorio")
    @Size(min = 8, max = 8, message = "El dni debe contener 8 digitos")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 50, message = "Los nombres deben tener entre 2 a 50 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos tienen entre 2 a 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El numero de telefono es obligatorio")
    @Size(min = 9, max = 9, message = "El telefono debe tener 9 digitos")
    private String telefono;

    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;
}

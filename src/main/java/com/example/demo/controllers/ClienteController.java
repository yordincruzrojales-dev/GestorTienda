package com.example.demo.controllers;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.dtos.cliente.ClienteResponseDTO;
import com.example.demo.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para la gestión de clientes en el sistema")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Registrar cliente", description = "Crea un nuevo registro de cliente en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o no pasan las validaciones"),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un cliente con el mismo DNI")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO){
        ClienteResponseDTO nuevoCliente = clienteService.registrarCliente(clienteRequestDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los clientes", description = "Obtiene la lista completa de clientes registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes(){
        List<ClienteResponseDTO> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Buscar cliente por ID", description = "Recupera los detalles de un cliente a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el ID especificado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable Long id){
        ClienteResponseDTO cliente = clienteService.buscarPorIdDTO(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Buscar cliente por DNI", description = "Recupera los datos de un cliente utilizando su número de DNI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el DNI especificado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ClienteResponseDTO> buscarPorDni(@RequestParam String dni){
        ClienteResponseDTO cliente = clienteService.buscarPorDni(dni);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO clienteRequestDTO){
        ClienteResponseDTO clienteModificado = clienteService.actualizarCliente(id, clienteRequestDTO);
        return ResponseEntity.ok(clienteModificado);
    }

    @Operation(summary = "Desactivar cliente", description = "Realiza un borrado lógico desactivando el cliente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente desactivado exitosamente (sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado para desactivar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarCliente(@PathVariable Long id){
        clienteService.desactivarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar cliente", description = "Vuelve a habilitar el estado activo de un cliente desactivado previamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente reactivado exitosamente (sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado para reactivar")
    })
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> reactivarCliente(@PathVariable Long id){
        clienteService.reactivarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
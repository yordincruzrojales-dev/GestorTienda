package com.example.demo.controllers;

import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.services.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Endpoints para el registro, gestión y consulta transaccional de ventas")
public class VentaController {

    private final VentaService ventaService;

    @Operation(summary = "Registrar venta", description = "Procesa y registra una nueva venta transaccional en el sistema, descontando el stock correspondiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (lista de detalles vacía o parámetros incorrectos)"),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente para realizar la transacción")
    })
    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO){
        VentaResponseDTO ventaNueva = ventaService.registrarVenta(ventaRequestDTO);
        return new ResponseEntity<>(ventaNueva, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar venta por ID", description = "Obtiene los detalles del comprobante de venta e ítems a partir del ID de la transacción.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada con el ID especificado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscarVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las ventas", description = "Recupera la lista completa de transacciones de venta registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(){
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @Operation(summary = "Listar ventas por cliente", description = "Obtiene el historial de compras asociadas al ID de un cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<VentaResponseDTO>> listarPorCliente(@RequestParam Long idCliente){
        return ResponseEntity.ok(ventaService.listarPorCliente(idCliente));
    }

    @Operation(summary = "Listar ventas con total menor o igual a", description = "Filtra transacciones cuyo importe total sea menor o igual al monto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/total-menor")
    public ResponseEntity<List<VentaResponseDTO>> listarTotalMenor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMenorIgualTotal(total));
    }

    @Operation(summary = "Listar ventas con total mayor a", description = "Filtra transacciones cuyo importe total supere el monto indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/total-mayor")
    public ResponseEntity<List<VentaResponseDTO>> listarTotalMayor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMayorTotal(total));
    }

    @Operation(summary = "Listar ventas por rango de fechas", description = "Consulta las transacciones registradas en un período determinado (fechas en formato ISO DATE_TIME).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Formato de fecha/hora no válido")
    })
    @GetMapping("/buscar-por-fechas")
    public ResponseEntity<List<VentaResponseDTO>> listarEntreFechas(
            @Parameter(description = "Fecha y hora inicial (Ej. 2026-10-15T00:00:00)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,

            @Parameter(description = "Fecha y hora final (Ej. 2026-10-15T23:59:59)", schema = @Schema(type = "string", format = "date-time"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<VentaResponseDTO> ventas = ventaService.listarEntreFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}
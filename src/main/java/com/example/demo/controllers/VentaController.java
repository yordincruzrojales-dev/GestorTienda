package com.example.demo.controllers;

import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.models.Venta;
import com.example.demo.services.VentaService;
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
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO){
        VentaResponseDTO ventaNueva = ventaService.registrarVenta(ventaRequestDTO);
        return new ResponseEntity<>(ventaNueva, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscarVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(){
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<VentaResponseDTO >> listarPorCliente(@RequestParam Long idCliente){
        return ResponseEntity.ok(ventaService.listarPorCliente(idCliente));
    }

    @GetMapping("/total-menor")
    public ResponseEntity<List<VentaResponseDTO>> listarTotalMenor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMenorIgualTotal(total));
    }

    @GetMapping("/total-mayor")
    public ResponseEntity<List<VentaResponseDTO>> listarTotalMayor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMayorTotal(total));
    }

    @GetMapping("/buscar-por-fechas")
    public ResponseEntity<List<VentaResponseDTO>> listarEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<VentaResponseDTO> ventas = ventaService.listarEntreFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}

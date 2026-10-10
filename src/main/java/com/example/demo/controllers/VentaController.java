package com.example.demo.controllers;

import com.example.demo.models.Venta;
import com.example.demo.services.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta){
        Venta ventaNueva = ventaService.registrarVenta(venta);
        return new ResponseEntity<>(ventaNueva, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas(){
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Venta>> listarPorCliente(@RequestParam Long idCliente){
        return ResponseEntity.ok(ventaService.listarPorCliente(idCliente));
    }

    @GetMapping("/total-menor")
    public ResponseEntity<List<Venta>> listarTotalMenor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMenorIgualTotal(total));
    }

    @GetMapping("/total-mayor")
    public ResponseEntity<List<Venta>> listarTotalMayor(@RequestParam BigDecimal total){
        return ResponseEntity.ok(ventaService.listarMayorTotal(total));
    }

    @GetMapping("/buscar-por-fechas")
    public ResponseEntity<List<Venta>> listarEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<Venta> ventas = ventaService.listarEntreFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
}

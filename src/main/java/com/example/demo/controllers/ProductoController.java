package com.example.demo.controllers;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> registrarProducto(@Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        ProductoResponseDTO productoRegistrado = productoService.registrarProducto(productoRequestDTO);
        return new ResponseEntity<>(productoRegistrado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosInactivos() {
        return ResponseEntity.ok(productoService.listarProductosInactivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorIdDTO(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ProductoResponseDTO> buscarPorCodigoBarras(@RequestParam String codigoBarras) {
        return ResponseEntity.ok(productoService.buscarPorCodigoBarras(codigoBarras));
    }

    @GetMapping("/stock-mayor")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorStockMayor(@RequestParam Integer stock) {
        return ResponseEntity.ok(productoService.listarMayorStock(stock));
    }

    @GetMapping("/stock-menor")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorStockMenor(@RequestParam Integer stock) {
        return ResponseEntity.ok(productoService.listarMenorIgualStock(stock));
    }

    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.listarPorNombre(nombre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> reactivarProducto(@PathVariable Long id) {
        productoService.reactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
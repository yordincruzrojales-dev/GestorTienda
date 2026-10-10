package com.example.demo.controllers;

import com.example.demo.models.Producto;
import com.example.demo.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> registrarProducto(@RequestBody Producto producto){
        Producto productoRegistrado = productoService.registrarProducto(producto);

        return new ResponseEntity<>(productoRegistrado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos(){
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarProducto(@PathVariable Long id){
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Producto> buscarPorCodigoBarras(@RequestParam String codigoBarras){
        return ResponseEntity.ok(productoService.buscarPorCodigoBarras(codigoBarras));
    }

    @GetMapping("/stock-mayor")
    public ResponseEntity<List<Producto>> buscarPorStockMayor(@RequestParam Integer stock){
        return ResponseEntity.ok(productoService.listarMayorStock(stock));
    }

    @GetMapping("/stock-menor")
    public ResponseEntity<List<Producto>> buscarPorStockMenor(@RequestParam Integer stock){
        return ResponseEntity.ok(productoService.listarMenorIgualStock(stock));
    }

    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre){
        return ResponseEntity.ok(productoService.listarPorNombre(nombre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto){
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id){
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> reactivarProducto(@PathVariable Long id){
        productoService.reactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}

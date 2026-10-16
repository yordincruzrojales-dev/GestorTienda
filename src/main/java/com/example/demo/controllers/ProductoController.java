package com.example.demo.controllers;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.services.ProductoService;
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
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión del catálogo de productos e inventario")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Registrar producto", description = "Crea un nuevo producto en el catálogo con sus existencias iniciales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un producto con el mismo código de barras")
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> registrarProducto(@Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        ProductoResponseDTO productoRegistrado = productoService.registrarProducto(productoRequestDTO);
        return new ResponseEntity<>(productoRegistrado, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar productos activos", description = "Obtiene la lista de todos los productos que están activos en el catálogo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @Operation(summary = "Listar productos inactivos", description = "Obtiene la lista de productos que han sido desactivados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/inactivos")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosInactivos() {
        return ResponseEntity.ok(productoService.listarProductosInactivos());
    }

    @Operation(summary = "Buscar producto por ID", description = "Recupera los detalles de un producto a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID especificado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorIdDTO(id));
    }

    @Operation(summary = "Buscar producto por código de barras", description = "Recupera los datos de un producto mediante su código de barras único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado con el código de barras especificado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ProductoResponseDTO> buscarPorCodigoBarras(@RequestParam String codigoBarras) {
        return ResponseEntity.ok(productoService.buscarPorCodigoBarras(codigoBarras));
    }

    @Operation(summary = "Buscar productos por stock mayor a", description = "Filtra y devuelve los productos cuyo stock actual sea superior al valor indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/stock-mayor")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorStockMayor(@RequestParam Integer stock) {
        return ResponseEntity.ok(productoService.listarMayorStock(stock));
    }

    @Operation(summary = "Buscar productos por stock menor o igual a", description = "Filtra y devuelve los productos cuyo stock sea menor o igual al valor especificado (útil para alertas de bajo inventario).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/stock-menor")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorStockMenor(@RequestParam Integer stock) {
        return ResponseEntity.ok(productoService.listarMenorIgualStock(stock));
    }

    @Operation(summary = "Buscar productos por nombre", description = "Filtra los productos cuya descripción o nombre coincida con la coincidencia enviada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente")
    })
    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.listarPorNombre(nombre));
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoRequestDTO));
    }

    @Operation(summary = "Desactivar producto", description = "Realiza un borrado lógico desactivando el producto por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto desactivado exitosamente (sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para desactivar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar producto", description = "Vuelve a habilitar el estado activo de un producto desactivado previamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto reactivado exitosamente (sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para reactivar")
    })
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> reactivarProducto(@PathVariable Long id) {
        productoService.reactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
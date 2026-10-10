package com.example.demo.services;

import com.example.demo.models.Producto;
import com.example.demo.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public Producto registrarProducto(Producto producto) {
        if (productoRepository.existsByCodigoBarrasAndActivoTrue(producto.getCodigoBarras())) {
            throw new IllegalArgumentException("Ya existe un producto con el código de barras: " + producto.getCodigoBarras());
        }

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public Producto buscarPorCodigoBarras(String codigo) {
        return productoRepository.findByCodigoBarrasAndActivoTrue(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el código de barras: " + codigo));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductosInactivos() {
        return productoRepository.findByActivoFalse();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarMenorIgualStock(Integer stock) {
        return productoRepository.findByStockLessThanEqualAndActivoTrue(stock);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarMayorStock(Integer stock) {
        return productoRepository.findByStockGreaterThanAndActivoTrue(stock);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto producto) {
        Producto productoExistente = buscarPorId(id);

        if (!productoExistente.getCodigoBarras().equals(producto.getCodigoBarras())
                && productoRepository.existsByCodigoBarrasAndActivoTrue(producto.getCodigoBarras())) {
            throw new IllegalArgumentException("El código de barras " + producto.getCodigoBarras() + " está registrado en otro producto");
        }

        if (producto.getCodigoBarras() != null){
            productoExistente.setCodigoBarras(producto.getCodigoBarras());
        }

        if (producto.getNombre() != null){
            productoExistente.setNombre(producto.getNombre());
        }

        if (producto.getUnidadMedida() != null){
            productoExistente.setUnidadMedida(producto.getUnidadMedida());
        }

        if (producto.getPrecio() != null){
            productoExistente.setPrecio(producto.getPrecio());
        }

        if (producto.getStock() != null && producto.getStock() >= 0){
            productoExistente.setStock(producto.getStock());
        }

        return productoRepository.save(productoExistente);
    }

    @Transactional
    public void desactivarProducto(Long id){
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe o no esta actio"));

        producto.setActivo(false);

        productoRepository.save(producto);
    }

    @Transactional
    public void reactivarProducto(Long id){
        Producto producto = productoRepository.findByIdAndActivoFalse(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe o no esta desactivado"));

        producto.setActivo(true);

        productoRepository.save(producto);
    }
}
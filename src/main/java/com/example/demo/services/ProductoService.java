package com.example.demo.services;

import com.example.demo.models.Producto;
import com.example.demo.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public Producto registrarProducto(Producto producto) {
        if (productoRepository.existsByCodigoBarras(producto.getCodigoBarras())) {
            throw new IllegalArgumentException("Ya existe un producto con el código de barras: " + producto.getCodigoBarras());
        }

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public Producto buscarPorCodigoBarras(String codigo) {
        return productoRepository.findByCodigoBarras(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el código de barras: " + codigo));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarMenorIgualStock(Integer stock) {
        return productoRepository.findByStockLessThanEqual(stock);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarMayorStock(Integer stock) {
        return productoRepository.findByStockGreaterThan(stock);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto producto) {
        Producto productoExistente = buscarPorId(id);

        if (!productoExistente.getCodigoBarras().equals(producto.getCodigoBarras())
                && productoRepository.existsByCodigoBarras(producto.getCodigoBarras())) {
            throw new IllegalArgumentException("El código de barras " + producto.getCodigoBarras() + " está registrado en otro producto");
        }

        productoExistente.setNombre(producto.getNombre());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setCodigoBarras(producto.getCodigoBarras());

        return productoRepository.save(productoExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. El producto con ID " + id + " no existe");
        }

        productoRepository.deleteById(id);
    }
}
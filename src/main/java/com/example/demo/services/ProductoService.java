package com.example.demo.services;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ProductoMapper;
import com.example.demo.models.Producto;
import com.example.demo.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO productoRequestDTO) {
        if (productoRepository.existsByCodigoBarras(productoRequestDTO.getCodigoBarras())) {
            throw new DuplicateResourceException("Ya existe un producto registrado con el código de barras: " + productoRequestDTO.getCodigoBarras());
        }

        Producto productoNuevo = productoMapper.toEntity(productoRequestDTO);
        Producto productoGuardado = productoRepository.save(productoNuevo);

        return productoMapper.toResponseDTO(productoGuardado);
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorIdDTO(Long id) {
        Producto producto = buscarPorId(id);
        return productoMapper.toResponseDTO(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorCodigoBarras(String codigo) {
        Producto producto = productoRepository.findByCodigoBarrasAndActivoTrue(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el código de barras: " + codigo));
        return productoMapper.toResponseDTO(producto);
    }

    @Transactional(readOnly = true)
    public Map<Long, Producto> obtenerProductosPorIdsMap(List<Long> ids) {
        List<Long> idsUnicos = ids.stream().distinct().toList();

        List<Producto> productos = productoRepository.findByIdInAndActivoTrue(idsUnicos);

        if (productos.size() != idsUnicos.size()) {
            throw new ResourceNotFoundException("Uno o más productos no existen o se encuentran inactivos.");
        }

        return productos.stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosInactivos() {
        return productoRepository.findByActivoFalse()
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarMenorIgualStock(Integer stock) {
        return productoRepository.findByStockLessThanEqualAndActivoTrue(stock)
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarMayorStock(Integer stock) {
        return productoRepository.findByStockGreaterThanAndActivoTrue(stock)
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO productoRequestDTO) {
        Producto productoExistente = buscarPorId(id);

        if (!productoExistente.getCodigoBarras().equals(productoRequestDTO.getCodigoBarras())
                && productoRepository.existsByCodigoBarras(productoRequestDTO.getCodigoBarras())) {
            throw new DuplicateResourceException("El código de barras " + productoRequestDTO.getCodigoBarras() + " ya está registrado en otro producto.");
        }

        if (productoRequestDTO.getCodigoBarras() != null) {
            productoExistente.setCodigoBarras(productoRequestDTO.getCodigoBarras());
        }

        if (productoRequestDTO.getNombre() != null) {
            productoExistente.setNombre(productoRequestDTO.getNombre());
        }

        if (productoRequestDTO.getUnidadMedida() != null) {
            productoExistente.setUnidadMedida(productoRequestDTO.getUnidadMedida());
        }

        if (productoRequestDTO.getPrecio() != null) {
            productoExistente.setPrecio(productoRequestDTO.getPrecio());
        }

        if (productoRequestDTO.getStock() != null && productoRequestDTO.getStock() >= 0) {
            productoExistente.setStock(productoRequestDTO.getStock());
        }

        if (productoRequestDTO.getActivo() != null) {
            productoExistente.setActivo(productoRequestDTO.getActivo());
        }

        Producto productoModificado = productoRepository.save(productoExistente);

        return productoMapper.toResponseDTO(productoModificado);
    }

    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe o no está activado."));
        producto.setActivo(false);

        productoRepository.save(producto);
    }

    @Transactional
    public void reactivarProducto(Long id) {
        Producto producto = productoRepository.findByIdAndActivoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe o no está desactivado."));
        producto.setActivo(true);

        productoRepository.save(producto);
    }
}
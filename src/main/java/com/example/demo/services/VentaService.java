package com.example.demo.services;

import com.example.demo.dtos.venta.DetalleVentaRequestDTO;
import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.StockInsuficienteException;
import com.example.demo.mappers.VentaMapper;
import com.example.demo.models.Cliente;
import com.example.demo.models.DetalleVenta;
import com.example.demo.models.Producto;
import com.example.demo.models.Venta;
import com.example.demo.repositories.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final VentaMapper ventaMapper;

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO ventaRequestDTO){

        if (ventaRequestDTO.getDetallesVenta() == null || ventaRequestDTO.getDetallesVenta().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle de producto");
        }

        Cliente cliente = clienteService.buscarPorId(ventaRequestDTO.getIdCliente());

        Map<Long, Producto> productos = productoService.obtenerProductosPorIdsMap(
                ventaRequestDTO.getDetallesVenta().stream().map(DetalleVentaRequestDTO::getIdProducto).toList()
        );

        for (DetalleVentaRequestDTO dv : ventaRequestDTO.getDetallesVenta()){
            Producto producto = productos.get(dv.getIdProducto());

            if (producto.getStock() < dv.getCantidad()){
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ", Solicitado: " + dv.getCantidad());
            }

            producto.setStock(producto.getStock() - dv.getCantidad());
        }

        Venta venta = ventaMapper.toVentaEntity(ventaRequestDTO, cliente, productos);
        Venta ventaCreada = ventaRepository.save(venta);

        return ventaMapper.toVentaResponseDTO(ventaCreada);
    }

    @Transactional(readOnly = true)
    public VentaResponseDTO buscarPorId(Long id){
        Venta ventaEncontrada = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrado por el ID: " + id));

        return ventaMapper.toVentaResponseDTO(ventaEncontrada);
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarVentas(){
        return ventaRepository.findAll().stream().map(ventaMapper::toVentaResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarPorCliente(Long id){
        return ventaRepository.findByClienteId(id).stream().map(ventaMapper::toVentaResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarMenorIgualTotal(BigDecimal total){
        return ventaRepository.findByTotalLessThanEqual(total).stream().map(ventaMapper::toVentaResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarMayorTotal(BigDecimal total){
        return ventaRepository.findByTotalGreaterThan(total).stream().map(ventaMapper::toVentaResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFinal){
        return ventaRepository.findByFechaHoraBetween(fechaInicio, fechaFinal).stream().map(ventaMapper::toVentaResponseDTO).toList();
    }
}

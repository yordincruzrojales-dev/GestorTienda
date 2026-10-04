package com.example.demo.services;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    @Transactional
    public Venta regitrarVenta(Venta venta){

        Cliente cliente = clienteService.buscarPorDni(venta.getCliente().getDni());
        venta.setCliente(cliente);

        List<DetalleVenta> listaDetalleVenta = venta.getDetalles();

        if (listaDetalleVenta == null || listaDetalleVenta.isEmpty()){
            throw new IllegalArgumentException("La venta denbe contener al menos un detalle de producto");
        }


        for (DetalleVenta dv : listaDetalleVenta){
            Producto producto = productoService.buscarPorId(dv.getProducto().getId());

            if (producto.getStock() < dv.getCantidad()){
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - dv.getCantidad());

            dv.setProducto(producto);
            dv.setPrecioUnitario(producto.getPrecio());
            dv.setVenta(venta);

            dv.calcularSubtotal();
        }

        venta.prePersist();
        venta.calcularTotal();

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(Long id){
        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrado por el ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Venta> listarPorCliente(Long id){
        return ventaRepository.findByClienteId(id);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarMenorIgualTotal(BigDecimal total){
        return ventaRepository.findByTotalLessThanEqual(total);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarMayorTotal(BigDecimal total){
        return ventaRepository.findByTotalGreaterThan(total);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFinal){
        return ventaRepository.findByFechaHoraBetween(fechaInicio, fechaFinal);
    }
}

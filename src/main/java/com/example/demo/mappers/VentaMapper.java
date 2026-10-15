package com.example.demo.mappers;

import com.example.demo.dtos.venta.DetalleVentaRequestDTO;
import com.example.demo.dtos.venta.DetalleVentaResponseDTO;
import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.models.Cliente;
import com.example.demo.models.DetalleVenta;
import com.example.demo.models.Producto;
import com.example.demo.models.Venta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class VentaMapper {

    public DetalleVenta toDetalleEntity(DetalleVentaRequestDTO dto, Producto producto){
        if (dto == null) return null;

        return DetalleVenta.builder()
                .cantidad(dto.getCantidad())
                .producto(producto)
                .precioUnitario(producto != null ? producto.getPrecio() : null)
                .build();
    }

    public DetalleVentaResponseDTO toDetalleResponseDTO(DetalleVenta entity){
        if (entity == null) return null;

        return DetalleVentaResponseDTO.builder()
                .id(entity.getId())
                .idProducto(entity.getProducto() != null ? entity.getProducto().getId() : null)
                .nombreProducto(entity.getProducto() != null ? entity.getProducto().getNombre() : null)
                .cantidad(entity.getCantidad())
                .precioUnitario(entity.getPrecioUnitario())
                .subtotal(entity.getSubtotal())
                .build();
    }

    public Venta toVentaEntity(VentaRequestDTO dto, Cliente cliente, Map<Long, Producto> productos){
        if (dto == null) return null;

        Venta venta = Venta.builder()
                .cliente(cliente)
                .build();

        List<DetalleVenta> detalles = dto.getDetallesVenta().stream()
                .map(dv -> {
                    Producto producto = productos.get(dv.getIdProducto());
                    DetalleVenta detalleVenta = toDetalleEntity(dv, producto);
                    if (detalleVenta != null){
                        detalleVenta.setVenta(venta);
                    }
                    return detalleVenta;
                }).toList();

        venta.setDetalles(detalles);
        return venta;
    }

    public VentaResponseDTO toVentaResponseDTO(Venta venta){
        if (venta == null) return null;

        return VentaResponseDTO.builder()
                .id(venta.getId())
                .idCliente(venta.getCliente() != null ? venta.getCliente().getId() : null)
                .detallesVenta(venta.getDetalles() != null ?
                        venta.getDetalles().stream().map(this::toDetalleResponseDTO).toList() : List.of())
                .total(venta.getTotal())
                .fechaHora(venta.getFechaHora())
                .build();
    }

}

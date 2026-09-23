package com.example.demo.models;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    private Venta venta;
    private Producto producto;
    private int cantidad;

    public BigDecimal obtenerSubtotal() {
        return producto.getPrecio().multiply(new BigDecimal(cantidad));
    }

}

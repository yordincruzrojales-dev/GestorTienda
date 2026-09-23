package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venta {
    private Long id;

    @ManyToOne
    private Cliente cliente;

    private List<DetalleVenta> listaProductos;

    private LocalDate fecha;
    private LocalTime hora;

    public BigDecimal obtenerPrecioTotal(){
        BigDecimal suma = new BigDecimal(0);

        for(DetalleVenta dv : listaProductos){
            suma = suma.add(dv.obtenerSubtotal());
        }

        return suma;
    }
}

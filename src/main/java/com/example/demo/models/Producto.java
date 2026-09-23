package com.example.demo.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Producto {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private String unidadMedida;
    private BigDecimal precio;
    private int stock;

    public Producto(Long id, String codigoBarras, String nombre, String unidadMedida, BigDecimal precio, int stock) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.precio = precio;
        this.stock = stock;
    }
}

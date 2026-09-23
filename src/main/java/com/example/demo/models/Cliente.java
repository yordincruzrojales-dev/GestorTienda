package com.example.demo.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cliente {

    private Long id;
    private String codigo;
    private String nombres;
    private String apellidos;

    public Cliente(Long id, String codigo, String nombres, String apellidos) {
        this.id = id;
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }
}

package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "Los detalles de la venta son obligatorios")
    @Size(min = 1, message = "La lista de ventas debe tener al menos un producto")
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Valid
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    @PreUpdate
    public void preGuardar() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }

        calcularTotal();
    }

    public void calcularTotal() {
        BigDecimal suma = BigDecimal.ZERO;
        if (detalles != null) {
            for (DetalleVenta dv : detalles) {
                dv.calcularSubtotal();
                if (dv.getSubtotal() != null){
                    suma = suma.add(dv.getSubtotal());
                }
            }
        }
        this.total = suma;
    }
}

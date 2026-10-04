package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import com.example.demo.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteId(Long clienteId);

    List<Venta> findByTotalLessThanEqual(BigDecimal total);

    List<Venta> findByTotalGreaterThan(BigDecimal total);
    
    List<Venta> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
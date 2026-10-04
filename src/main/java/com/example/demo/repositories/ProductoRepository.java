package com.example.demo.repositories;

import com.example.demo.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigoBarras(String codigoBarras);

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    List<Producto> findByStockLessThanEqual(Integer stock);

    List<Producto> findByStockGreaterThan(Integer stock);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
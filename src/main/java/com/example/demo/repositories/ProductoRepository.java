package com.example.demo.repositories;

import com.example.demo.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByIdAndActivoTrue(Long id);
    boolean existsByCodigoBarrasAndActivoTrue(String codigoBarras);
    boolean existsByIdAndActivoFalse(Long id);

    Optional<Producto> findByIdAndActivoTrue(Long id);
    Optional<Producto> findByIdAndActivoFalse(Long id);
    Optional<Producto> findByCodigoBarrasAndActivoTrue(String codigoBarras);

    List<Producto> findByActivoTrue();
    List<Producto> findByActivoFalse();

    List<Producto> findByStockLessThanEqualAndActivoTrue(Integer stock);

    List<Producto> findByStockGreaterThanAndActivoTrue(Integer stock);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
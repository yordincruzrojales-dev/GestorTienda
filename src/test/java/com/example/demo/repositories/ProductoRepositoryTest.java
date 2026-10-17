package com.example.demo.repositories;

import com.example.demo.models.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Pruebas de Persistencia para ProductoRepository")
class ProductoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;

    private Producto productoActivo1;
    private Producto productoActivo2;
    private Producto productoInactivo;

    @BeforeEach
    void setUp() {
        productoActivo1 = Producto.builder()
                .codigoBarras("7751234567890")
                .nombre("Arroz Extra 1kg")
                .unidadMedida("Bolsa")
                .precio(new BigDecimal("4.50"))
                .stock(100)
                .activo(true)
                .build();

        productoActivo2 = Producto.builder()
                .codigoBarras("7759876543210")
                .nombre("Aceite Vegetal 1L")
                .unidadMedida("Botella")
                .precio(new BigDecimal("8.00"))
                .stock(10)
                .activo(true)
                .build();

        productoInactivo = Producto.builder()
                .codigoBarras("7751112223334")
                .nombre("Arroz Integral 1kg")
                .unidadMedida("Bolsa")
                .precio(new BigDecimal("6.00"))
                .stock(80)
                .activo(false)
                .build();
    }

    @Test
    @DisplayName("Debe buscar un producto activo por código de barras")
    void findByCodigoBarrasAndActivoTrue_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(productoActivo1);

        // ACT
        Optional<Producto> resultado = productoRepository.findByCodigoBarrasAndActivoTrue("7751234567890");

        // ASSERT
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Arroz Extra 1kg");
        assertThat(resultado.get().getActivo()).isTrue();
    }

    @Test
    @DisplayName("No debe retornar el producto por código de barras si está inactivo")
    void findByCodigoBarrasAndActivoTrue_ProductoInactivo_RetornaEmpty() {
        // ARRANGE
        entityManager.persistAndFlush(productoInactivo);

        // ACT
        Optional<Producto> resultado = productoRepository.findByCodigoBarrasAndActivoTrue("7751112223334");

        // ASSERT
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe filtrar productos activos con stock estrictamente mayor al valor dado")
    void findByStockGreaterThanAndActivoTrue_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(productoActivo1); // Stock 100
        entityManager.persistAndFlush(productoActivo2); // Stock 10
        entityManager.persistAndFlush(productoInactivo); // Stock 80 (Inactivo)

        // ACT
        List<Producto> resultados = productoRepository.findByStockGreaterThanAndActivoTrue(50);

        // ASSERT
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getCodigoBarras()).isEqualTo("7751234567890");
    }

    @Test
    @DisplayName("Debe buscar productos activos cuyo nombre contenga la cadena buscada (IgnoreCase)")
    void findByNombreContainingIgnoreCaseAndActivoTrue_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(productoActivo1); // "Arroz Extra 1kg"
        entityManager.persistAndFlush(productoInactivo); // "Arroz Integral 1kg" (Inactivo)

        // ACT
        List<Producto> resultados = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("arroz");

        // ASSERT
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Arroz Extra 1kg");
    }
}
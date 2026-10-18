package com.example.demo.repositories;

import com.example.demo.models.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Pruebas del Repositorio de Productos")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    private Producto productoActivoConStock;
    private Producto productoActivoBajoStock;
    private Producto productoInactivo;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        productoActivoConStock = Producto.builder()
                .codigoBarras("7751234567890")
                .nombre("Leche Gloria Entera 1L")
                .unidadMedida("Unidad")
                .precio(new BigDecimal("4.50"))
                .stock(50)
                .activo(true)
                .build();

        productoActivoBajoStock = Producto.builder()
                .codigoBarras("7750000000001")
                .nombre("Galletas Soda Field")
                .unidadMedida("Paquete")
                .precio(new BigDecimal("1.20"))
                .stock(5)
                .activo(true)
                .build();

        productoInactivo = Producto.builder()
                .codigoBarras("7759999999999")
                .nombre("Producto Descontinuado")
                .unidadMedida("Unidad")
                .precio(new BigDecimal("10.00"))
                .stock(0)
                .activo(false)
                .build();
    }

    @Nested
    @DisplayName("Métodos ExistsBy")
    class ExistsByTests {

        @Test
        @DisplayName("Debe retornar true al comprobar existencia de ID de producto activo")
        void existsByIdAndActivoTrue_DeberiaRetornarTrue() {
            Producto guardado = productoRepository.save(productoActivoConStock);

            boolean existe = productoRepository.existsByIdAndActivoTrue(guardado.getId());

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false al buscar ID activo de un producto inactivo")
        void existsByIdAndActivoTrue_ProductoInactivo_DeberiaRetornarFalse() {
            Producto guardado = productoRepository.save(productoInactivo);

            boolean existe = productoRepository.existsByIdAndActivoTrue(guardado.getId());

            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("Debe retornar true al verificar código de barras activo")
        void existsByCodigoBarrasAndActivoTrue_DeberiaRetornarTrue() {
            productoRepository.save(productoActivoConStock);

            boolean existe = productoRepository.existsByCodigoBarrasAndActivoTrue("7751234567890");

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true al verificar código de barras independientemente del estado")
        void existsByCodigoBarras_DeberiaRetornarTrue() {
            productoRepository.save(productoInactivo);

            boolean existe = productoRepository.existsByCodigoBarras("7759999999999");

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true al verificar por ID de producto inactivo")
        void existsByIdAndActivoFalse_DeberiaRetornarTrue() {
            Producto guardado = productoRepository.save(productoInactivo);

            boolean existe = productoRepository.existsByIdAndActivoFalse(guardado.getId());

            assertThat(existe).isTrue();
        }
    }

    @Nested
    @DisplayName("Métodos FindBy Optional")
    class FindByOptionalTests {

        @Test
        @DisplayName("Debe buscar por ID y activo=true de forma exitosa")
        void findByIdAndActivoTrue_DeberiaEncontrarProducto() {
            Producto guardado = productoRepository.save(productoActivoConStock);

            Optional<Producto> resultado = productoRepository.findByIdAndActivoTrue(guardado.getId());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNombre()).isEqualTo("Leche Gloria Entera 1L");
        }

        @Test
        @DisplayName("Debe buscar por ID y activo=false de forma exitosa")
        void findByIdAndActivoFalse_DeberiaEncontrarProducto() {
            Producto guardado = productoRepository.save(productoInactivo);

            Optional<Producto> resultado = productoRepository.findByIdAndActivoFalse(guardado.getId());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNombre()).isEqualTo("Producto Descontinuado");
        }

        @Test
        @DisplayName("Debe buscar por código de barras activo exitosamente")
        void findByCodigoBarrasAndActivoTrue_DeberiaEncontrarProducto() {
            productoRepository.save(productoActivoConStock);

            Optional<Producto> resultado = productoRepository.findByCodigoBarrasAndActivoTrue("7751234567890");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getCodigoBarras()).isEqualTo("7751234567890");
        }
    }

    @Nested
    @DisplayName("Métodos FindBy Listas y Filtros Avanzados")
    class FindByListTests {

        @Test
        @DisplayName("Debe listar únicamente los productos activos")
        void findByActivoTrue_DeberiaRetornarListaActivos() {
            productoRepository.save(productoActivoConStock);
            productoRepository.save(productoActivoBajoStock);
            productoRepository.save(productoInactivo);

            List<Producto> activos = productoRepository.findByActivoTrue();

            assertThat(activos).hasSize(2)
                    .extracting(Producto::getCodigoBarras)
                    .containsExactlyInAnyOrder("7751234567890", "7750000000001");
        }

        @Test
        @DisplayName("Debe listar únicamente los productos inactivos")
        void findByActivoFalse_DeberiaRetornarListaInactivos() {
            productoRepository.save(productoActivoConStock);
            productoRepository.save(productoInactivo);

            List<Producto> inactivos = productoRepository.findByActivoFalse();

            assertThat(inactivos).hasSize(1)
                    .extracting(Producto::getCodigoBarras)
                    .containsExactly("7759999999999");
        }

        @Test
        @DisplayName("Debe buscar productos por lista de IDs activos")
        void findByIdInAndActivoTrue_DeberiaRetornarProductosMatching() {
            Producto prod1 = productoRepository.save(productoActivoConStock);
            Producto prod2 = productoRepository.save(productoActivoBajoStock);
            Producto prodInactivoGuardado = productoRepository.save(productoInactivo);

            List<Long> idsToSearch = List.of(prod1.getId(), prod2.getId(), prodInactivoGuardado.getId());

            List<Producto> resultado = productoRepository.findByIdInAndActivoTrue(idsToSearch);

            assertThat(resultado).hasSize(2)
                    .extracting(Producto::getId)
                    .containsExactlyInAnyOrder(prod1.getId(), prod2.getId());
        }

        @Test
        @DisplayName("Debe filtrar productos activos con stock menor o igual al umbral dado")
        void findByStockLessThanEqualAndActivoTrue_DeberiaFiltrarPorStock() {
            productoRepository.save(productoActivoConStock); // Stock: 50
            productoRepository.save(productoActivoBajoStock); // Stock: 5

            List<Producto> resultado = productoRepository.findByStockLessThanEqualAndActivoTrue(10);

            assertThat(resultado).hasSize(1)
                    .extracting(Producto::getNombre)
                    .containsExactly("Galletas Soda Field");
        }

        @Test
        @DisplayName("Debe filtrar productos activos con stock estrictamente mayor al valor dado")
        void findByStockGreaterThanAndActivoTrue_DeberiaFiltrarPorStock() {
            productoRepository.save(productoActivoConStock); // Stock: 50
            productoRepository.save(productoActivoBajoStock); // Stock: 5

            List<Producto> resultado = productoRepository.findByStockGreaterThanAndActivoTrue(10);

            assertThat(resultado).hasSize(1)
                    .extracting(Producto::getNombre)
                    .containsExactly("Leche Gloria Entera 1L");
        }

        @Test
        @DisplayName("Debe buscar productos por nombre ignorando mayúsculas y minúsculas (IgnoreCase)")
        void findByNombreContainingIgnoreCaseAndActivoTrue_DeberiaBuscarPorNombre() {
            productoRepository.save(productoActivoConStock); // "Leche Gloria Entera 1L"
            productoRepository.save(productoActivoBajoStock); // "Galletas Soda Field"

            List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("gloria");

            assertThat(resultado).hasSize(1)
                    .extracting(Producto::getCodigoBarras)
                    .containsExactly("7751234567890");
        }
    }
}
package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import com.example.demo.models.DetalleVenta;
import com.example.demo.models.Producto;
import com.example.demo.models.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Pruebas de Persistencia para VentaRepository")
class VentaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VentaRepository ventaRepository;

    private Cliente cliente;
    private Venta venta;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .telefono("987654321")
                .activo(true)
                .build();

        entityManager.persistAndFlush(cliente);

        Producto producto = Producto.builder()
                .codigoBarras("7751234567890")
                .nombre("Arroz Extra 1kg")
                .unidadMedida("Bolsa")
                .precio(new BigDecimal("4.50"))
                .stock(100)
                .activo(true)
                .build();

        entityManager.persistAndFlush(producto);

        DetalleVenta detalle = DetalleVenta.builder()
                .producto(producto)
                .cantidad(2)
                .precioUnitario(new BigDecimal("4.50"))
                .subtotal(new BigDecimal("9.00"))
                .build();

        venta = Venta.builder()
                .cliente(cliente)
                .total(new BigDecimal("9.00"))
                .fechaHora(LocalDateTime.now())
                .build();

        detalle.setVenta(venta);
        venta.setDetalles(List.of(detalle));
    }

    @Test
    @DisplayName("Debe guardar la venta en cascada con sus detalles y buscar por ID de cliente")
    void findByClienteId_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(venta);

        // ACT
        List<Venta> ventasCliente = ventaRepository.findByClienteId(cliente.getId());

        // ASSERT
        assertThat(ventasCliente).hasSize(1);
        assertThat(ventasCliente.get(0).getTotal()).isEqualByComparingTo("9.00");
        assertThat(ventasCliente.get(0).getDetalles()).hasSize(1);
    }

    @Test
    @DisplayName("Debe buscar ventas registradas dentro de un rango de fechas")
    void findByFechaHoraBetween_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(venta);

        LocalDateTime inicio = LocalDateTime.now().minusHours(1);
        LocalDateTime fin = LocalDateTime.now().plusHours(1);

        // ACT
        List<Venta> resultados = ventaRepository.findByFechaHoraBetween(inicio, fin);

        // ASSERT
        assertThat(resultados).isNotEmpty();
        assertThat(resultados.get(0).getCliente().getDni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Debe buscar ventas con un total menor o igual al valor especificado")
    void findByTotalLessThanEqual_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(venta);

        // ACT
        List<Venta> resultados = ventaRepository.findByTotalLessThanEqual(new BigDecimal("10.00"));

        // ASSERT
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getTotal()).isEqualByComparingTo("9.00");
    }

    @Test
    @DisplayName("Debe buscar ventas con un total estrictamente mayor al valor especificado")
    void findByTotalGreaterThan_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(venta);

        // ACT
        List<Venta> resultados = ventaRepository.findByTotalGreaterThan(new BigDecimal("5.00"));

        // ASSERT
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getTotal()).isEqualByComparingTo("9.00");
    }
}
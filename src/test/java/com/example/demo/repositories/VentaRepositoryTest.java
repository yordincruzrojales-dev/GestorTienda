package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import com.example.demo.models.DetalleVenta;
import com.example.demo.models.Producto;
import com.example.demo.models.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@DisplayName("Pruebas del Repositorio de Ventas")
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private Cliente clienteGuardado;
    private Producto productoGuardado;

    @BeforeEach
    void setUp() {
        ventaRepository.deleteAll();
        clienteRepository.deleteAll();
        productoRepository.deleteAll();

        // 1. Persistir Cliente base
        Cliente cliente = Cliente.builder()
                .dni("12345678")
                .nombres("Carlos")
                .apellidos("Mendoza")
                .telefono("987654321")
                .activo(true)
                .build();
        clienteGuardado = clienteRepository.save(cliente);

        // 2. Persistir Producto base
        Producto producto = Producto.builder()
                .codigoBarras("7751234567890")
                .nombre("Gaseosa Inca Kola 1.5L")
                .unidadMedida("Botella")
                .precio(new BigDecimal("7.50"))
                .stock(100)
                .activo(true)
                .build();
        productoGuardado = productoRepository.save(producto);
    }

    private Venta crearVentaConDetalles(Cliente cliente, Producto producto, int cantidad, BigDecimal precio, LocalDateTime fecha) {
        Venta venta = Venta.builder()
                .cliente(cliente)
                .fechaHora(fecha)
                .detalles(new ArrayList<>())
                .build();

        DetalleVenta detalle = DetalleVenta.builder()
                .venta(venta)
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(precio)
                .build();

        venta.getDetalles().add(detalle);
        return venta;
    }

    @Nested
    @DisplayName("Métodos de Búsqueda de Ventas")
    class BusquedaVentasTests {

        @Test
        @DisplayName("Debe guardar la venta en cascada con sus detalles y buscar por ID de cliente")
        void findByClienteId_DeberiaRetornarVentasDelCliente() {
            Venta venta = crearVentaConDetalles(clienteGuardado, productoGuardado, 2, new BigDecimal("7.50"), LocalDateTime.now());
            ventaRepository.save(venta);

            List<Venta> ventasCliente = ventaRepository.findByClienteId(clienteGuardado.getId());

            assertThat(ventasCliente).hasSize(1);
            assertThat(ventasCliente.get(0).getCliente().getId()).isEqualTo(clienteGuardado.getId());
            assertThat(ventasCliente.get(0).getDetalles()).hasSize(1);
            assertThat(ventasCliente.get(0).getTotal()).isEqualByComparingTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("Debe buscar ventas con un total menor o igual al valor especificado")
        void findByTotalLessThanEqual_DeberiaFiltrarPorTotal() {
            // Venta 1: Total = 15.00
            Venta ventaPequenia = crearVentaConDetalles(clienteGuardado, productoGuardado, 2, new BigDecimal("7.50"), LocalDateTime.now());
            // Venta 2: Total = 75.00
            Venta ventaGrande = crearVentaConDetalles(clienteGuardado, productoGuardado, 10, new BigDecimal("7.50"), LocalDateTime.now());

            ventaRepository.save(ventaPequenia);
            ventaRepository.save(ventaGrande);

            List<Venta> resultado = ventaRepository.findByTotalLessThanEqual(new BigDecimal("20.00"));

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getTotal()).isEqualByComparingTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("Debe buscar ventas con un total estrictamente mayor al valor especificado")
        void findByTotalGreaterThan_DeberiaFiltrarPorTotal() {
            Venta ventaPequenia = crearVentaConDetalles(clienteGuardado, productoGuardado, 2, new BigDecimal("7.50"), LocalDateTime.now());
            Venta ventaGrande = crearVentaConDetalles(clienteGuardado, productoGuardado, 10, new BigDecimal("7.50"), LocalDateTime.now());

            ventaRepository.save(ventaPequenia);
            ventaRepository.save(ventaGrande);

            List<Venta> resultado = ventaRepository.findByTotalGreaterThan(new BigDecimal("50.00"));

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getTotal()).isEqualByComparingTo(new BigDecimal("75.00"));
        }

        @Test
        @DisplayName("Debe buscar ventas registradas dentro de un rango de fechas")
        void findByFechaHoraBetween_DeberiaFiltrarPorRangoDeFechas() {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime haceAyer = ahora.minusDays(1);
            LocalDateTime haceTresDias = ahora.minusDays(3);

            Venta ventaReciente = crearVentaConDetalles(clienteGuardado, productoGuardado, 1, new BigDecimal("7.50"), haceAyer);
            Venta ventaAntigua = crearVentaConDetalles(clienteGuardado, productoGuardado, 1, new BigDecimal("7.50"), haceTresDias);

            ventaRepository.save(ventaReciente);
            ventaRepository.save(ventaAntigua);

            LocalDateTime fechaInicio = ahora.minusDays(2);
            LocalDateTime fechaFin = ahora;

            List<Venta> resultado = ventaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getFechaHora()).isAfterOrEqualTo(fechaInicio);
            assertThat(resultado.get(0).getFechaHora()).isBeforeOrEqualTo(fechaFin);
        }
    }
}
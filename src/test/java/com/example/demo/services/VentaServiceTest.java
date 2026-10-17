package com.example.demo.services;

import com.example.demo.dtos.venta.DetalleVentaRequestDTO;
import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.StockInsuficienteException;
import com.example.demo.mappers.VentaMapper;
import com.example.demo.models.Cliente;
import com.example.demo.models.Producto;
import com.example.demo.models.Venta;
import com.example.demo.repositories.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para VentaService")
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProductoService productoService;

    @Mock
    private VentaMapper ventaMapper;

    @InjectMocks
    private VentaService ventaService;

    private Cliente cliente;
    private Producto producto;
    private Venta venta;
    private VentaRequestDTO ventaRequestDTO;
    private VentaResponseDTO ventaResponseDTO;
    private DetalleVentaRequestDTO detalleVentaRequestDTO;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .activo(true)
                .build();

        producto = Producto.builder()
                .id(10L)
                .nombre("Teclado Mecánico")
                .precio(new BigDecimal("150.00"))
                .stock(20)
                .activo(true)
                .build();

        detalleVentaRequestDTO = DetalleVentaRequestDTO.builder()
                .idProducto(10L)
                .cantidad(2)
                .build();

        ventaRequestDTO = VentaRequestDTO.builder()
                .idCliente(1L)
                .detallesVenta(List.of(detalleVentaRequestDTO))
                .build();

        venta = Venta.builder()
                .id(100L)
                .cliente(cliente)
                .total(new BigDecimal("300.00"))
                .fechaHora(LocalDateTime.now())
                .build();

        ventaResponseDTO = VentaResponseDTO.builder()
                .id(100L)
                .nombresCliente("Juan Pérez")
                .total(new BigDecimal("300.00"))
                .fechaHora(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Pruebas para registrarVenta")
    class RegistrarVentaTests {

        @Test
        @DisplayName("Debe registrar la venta correctamente y reducir el stock del producto")
        void registrarVenta_Exito() {
            // ARRANGE
            when(clienteService.buscarPorId(1L)).thenReturn(cliente);
            when(productoService.obtenerProductosPorIdsMap(List.of(10L))).thenReturn(Map.of(10L, producto));
            when(ventaMapper.toVentaEntity(eq(ventaRequestDTO), eq(cliente), anyMap())).thenReturn(venta);
            when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            VentaResponseDTO resultado = ventaService.registrarVenta(ventaRequestDTO);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(100L, resultado.getId());
            assertEquals(18, producto.getStock()); // Verificamos que el stock disminuyó de 20 a 18 (20 - 2)

            verify(clienteService, times(1)).buscarPorId(1L);
            verify(productoService, times(1)).obtenerProductosPorIdsMap(List.of(10L));
            verify(ventaRepository, times(1)).save(any(Venta.class));
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando la lista de detalles está vacía o es nula")
        void registrarVenta_DetallesVacios_LanzaExcepcion() {
            // ARRANGE
            VentaRequestDTO dtoVacio = VentaRequestDTO.builder()
                    .idCliente(1L)
                    .detallesVenta(Collections.emptyList())
                    .build();

            // ACT & ASSERT
            assertThrows(IllegalArgumentException.class, () -> {
                ventaService.registrarVenta(dtoVacio);
            });

            verify(ventaRepository, never()).save(any(Venta.class));
        }

        @Test
        @DisplayName("Debe lanzar StockInsuficienteException cuando el stock disponible es menor a la cantidad solicitada")
        void registrarVenta_StockInsuficiente_LanzaExcepcion() {
            // ARRANGE
            DetalleVentaRequestDTO detalleSuperaStock = DetalleVentaRequestDTO.builder()
                    .idProducto(10L)
                    .cantidad(50) // Solicitamos 50 pero solo hay 20 en stock
                    .build();

            VentaRequestDTO dtoSinStock = VentaRequestDTO.builder()
                    .idCliente(1L)
                    .detallesVenta(List.of(detalleSuperaStock))
                    .build();

            when(clienteService.buscarPorId(1L)).thenReturn(cliente);
            when(productoService.obtenerProductosPorIdsMap(List.of(10L))).thenReturn(Map.of(10L, producto));

            // ACT & ASSERT
            assertThrows(StockInsuficienteException.class, () -> {
                ventaService.registrarVenta(dtoSinStock);
            });

            verify(ventaRepository, never()).save(any(Venta.class));
            assertEquals(20, producto.getStock()); // El stock NO debe verse alterado si falla
        }
    }

    @Nested
    @DisplayName("Pruebas para búsquedas y consultas de Venta")
    class BuscarVentaTests {

        @Test
        @DisplayName("buscarPorId: Debe retornar DTO cuando la venta existe")
        void buscarPorId_Exito() {
            // ARRANGE
            when(ventaRepository.findById(100L)).thenReturn(Optional.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            VentaResponseDTO resultado = ventaService.buscarPorId(100L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(100L, resultado.getId());
            verify(ventaRepository, times(1)).findById(100L);
        }

        @Test
        @DisplayName("buscarPorId: Debe lanzar ResourceNotFoundException cuando no existe")
        void buscarPorId_NoEncontrado_LanzaExcepcion() {
            // ARRANGE
            when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                ventaService.buscarPorId(999L);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas para listados de Ventas")
    class ListarVentasTests {

        @Test
        @DisplayName("listarVentas: Debe retornar todas las ventas registradas")
        void listarVentas_Exito() {
            // ARRANGE
            when(ventaRepository.findAll()).thenReturn(List.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            List<VentaResponseDTO> resultado = ventaService.listarVentas();

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(ventaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("listarPorCliente: Debe retornar las ventas pertenecientes a un cliente especifico")
        void listarPorCliente_Exito() {
            // ARRANGE
            when(ventaRepository.findByClienteId(1L)).thenReturn(List.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            List<VentaResponseDTO> resultado = ventaService.listarPorCliente(1L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(ventaRepository, times(1)).findByClienteId(1L);
        }

        @Test
        @DisplayName("listarMenorIgualTotal: Debe filtrar ventas por total menor o igual")
        void listarMenorIgualTotal_Exito() {
            // ARRANGE
            BigDecimal totalFiltro = new BigDecimal("500.00");
            when(ventaRepository.findByTotalLessThanEqual(totalFiltro)).thenReturn(List.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            List<VentaResponseDTO> resultado = ventaService.listarMenorIgualTotal(totalFiltro);

            // ASSERT
            assertNotNull(resultado);
            verify(ventaRepository, times(1)).findByTotalLessThanEqual(totalFiltro);
        }

        @Test
        @DisplayName("listarMayorTotal: Debe filtrar ventas por total mayor")
        void listarMayorTotal_Exito() {
            // ARRANGE
            BigDecimal totalFiltro = new BigDecimal("100.00");
            when(ventaRepository.findByTotalGreaterThan(totalFiltro)).thenReturn(List.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            List<VentaResponseDTO> resultado = ventaService.listarMayorTotal(totalFiltro);

            // ASSERT
            assertNotNull(resultado);
            verify(ventaRepository, times(1)).findByTotalGreaterThan(totalFiltro);
        }

        @Test
        @DisplayName("listarEntreFechas: Debe filtrar ventas en un rango de fechas")
        void listarEntreFechas_Exito() {
            // ARRANGE
            LocalDateTime inicio = LocalDateTime.now().minusDays(1);
            LocalDateTime fin = LocalDateTime.now().plusDays(1);

            when(ventaRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(List.of(venta));
            when(ventaMapper.toVentaResponseDTO(venta)).thenReturn(ventaResponseDTO);

            // ACT
            List<VentaResponseDTO> resultado = ventaService.listarEntreFechas(inicio, fin);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(ventaRepository, times(1)).findByFechaHoraBetween(inicio, fin);
        }
    }
}
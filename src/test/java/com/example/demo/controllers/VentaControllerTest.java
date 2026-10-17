package com.example.demo.controllers;

import com.example.demo.dtos.venta.DetalleVentaRequestDTO;
import com.example.demo.dtos.venta.DetalleVentaResponseDTO;
import com.example.demo.dtos.venta.VentaRequestDTO;
import com.example.demo.dtos.venta.VentaResponseDTO;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.StockInsuficienteException;
import com.example.demo.services.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
@DisplayName("Pruebas de Integración Web para VentaController")
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    private VentaRequestDTO ventaRequestDTO;
    private VentaResponseDTO ventaResponseDTO;
    private DetalleVentaRequestDTO detalleRequestDTO;
    private DetalleVentaResponseDTO detalleResponseDTO;

    @BeforeEach
    void setUp() {
        detalleRequestDTO = DetalleVentaRequestDTO.builder()
                .idProducto(10L)
                .cantidad(3)
                .build();

        ventaRequestDTO = VentaRequestDTO.builder()
                .idCliente(1L)
                .detallesVenta(List.of(detalleRequestDTO))
                .build();

        detalleResponseDTO = DetalleVentaResponseDTO.builder()
                .id(100L)
                .idProducto(10L)
                .nombreProducto("Arroz Extra 1kg")
                .cantidad(3)
                .precioUnitario(new BigDecimal("4.50"))
                .subtotal(new BigDecimal("13.50"))
                .build();

        ventaResponseDTO = VentaResponseDTO.builder()
                .id(50L)
                .idCliente(1L)
                .dniCliente("78945612")
                .nombresCliente("Juan Carlos")
                .detallesVenta(List.of(detalleResponseDTO))
                .total(new BigDecimal("13.50"))
                .fechaHora(LocalDateTime.of(2026, 10, 15, 14, 30, 0))
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/ventas - Registrar Venta")
    class RegistrarVentaTests {

        @Test
        @DisplayName("Debe retornar 201 Created cuando la venta es procesada correctamente")
        void registrarVenta_Exito() throws Exception {
            // ARRANGE
            when(ventaService.registrarVenta(any(VentaRequestDTO.class))).thenReturn(ventaResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ventaRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(50)))
                    .andExpect(jsonPath("$.idCliente", is(1)))
                    .andExpect(jsonPath("$.dniCliente", is("78945612")))
                    .andExpect(jsonPath("$.total", is(13.50)))
                    .andExpect(jsonPath("$.detallesVenta", hasSize(1)))
                    .andExpect(jsonPath("$.detallesVenta[0].nombreProducto", is("Arroz Extra 1kg")));

            verify(ventaService, times(1)).registrarVenta(any(VentaRequestDTO.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción de validación cuando la lista de detalles está vacía o nula (@Valid)")
        void registrarVenta_DetallesVacios_LanzaExcepcionValidacion() throws Exception {
            // ARRANGE
            VentaRequestDTO dtoInvalido = VentaRequestDTO.builder()
                    .idCliente(1L)
                    .detallesVenta(Collections.emptyList())
                    .build();

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andExpect(result -> assertInstanceOf(
                            MethodArgumentNotValidException.class,
                            result.getResolvedException()
                    ));

            verify(ventaService, never()).registrarVenta(any(VentaRequestDTO.class));
        }

        @Test
        @DisplayName("Debe capturar StockInsuficienteException cuando no hay existencias")
        void registrarVenta_StockInsuficiente_LanzaExcepcion() throws Exception {
            // ARRANGE
            when(ventaService.registrarVenta(any(VentaRequestDTO.class)))
                    .thenThrow(new StockInsuficienteException("Stock insuficiente para el producto: Arroz Extra 1kg"));

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ventaRequestDTO)))
                    .andExpect(result -> assertInstanceOf(
                            StockInsuficienteException.class,
                            result.getResolvedException()
                    ));

            verify(ventaService, times(1)).registrarVenta(any(VentaRequestDTO.class));
        }

        @Test
        @DisplayName("Debe capturar ResourceNotFoundException cuando el cliente o producto no existe")
        void registrarVenta_ClienteNoEncontrado_LanzaExcepcion() throws Exception {
            // ARRANGE
            when(ventaService.registrarVenta(any(VentaRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Cliente no encontrado con ID: 1"));

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/ventas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ventaRequestDTO)))
                    .andExpect(result -> assertInstanceOf(
                            ResourceNotFoundException.class,
                            result.getResolvedException()
                    ));

            verify(ventaService, times(1)).registrarVenta(any(VentaRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/ventas - Consultas y Filtros de Ventas")
    class ConsultarVentaTests {

        @Test
        @DisplayName("GET /api/v1/ventas/{id}: Debe retornar 200 OK al buscar venta por ID")
        void buscarVentaPorId_Exito() throws Exception {
            // ARRANGE
            when(ventaService.buscarPorId(50L)).thenReturn(ventaResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas/{id}", 50L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(50)))
                    .andExpect(jsonPath("$.nombresCliente", is("Juan Carlos")));

            verify(ventaService, times(1)).buscarPorId(50L);
        }

        @Test
        @DisplayName("GET /api/v1/ventas: Debe retornar 200 OK y la lista completa de ventas")
        void listarVentas_Exito() throws Exception {
            // ARRANGE
            when(ventaService.listarVentas()).thenReturn(List.of(ventaResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(50)));

            verify(ventaService, times(1)).listarVentas();
        }

        @Test
        @DisplayName("GET /api/v1/ventas/buscar?idCliente={id}: Debe listar ventas por cliente")
        void listarPorCliente_Exito() throws Exception {
            // ARRANGE
            when(ventaService.listarPorCliente(1L)).thenReturn(List.of(ventaResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas/buscar")
                            .param("idCliente", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].idCliente", is(1)));

            verify(ventaService, times(1)).listarPorCliente(1L);
        }

        @Test
        @DisplayName("GET /api/v1/ventas/total-menor?total={val}: Debe filtrar por total menor o igual")
        void listarTotalMenor_Exito() throws Exception {
            // ARRANGE
            when(ventaService.listarMenorIgualTotal(new BigDecimal("20.00"))).thenReturn(List.of(ventaResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas/total-menor")
                            .param("total", "20.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].total", is(13.50)));

            verify(ventaService, times(1)).listarMenorIgualTotal(new BigDecimal("20.00"));
        }

        @Test
        @DisplayName("GET /api/v1/ventas/total-mayor?total={val}: Debe filtrar por total estrictamente mayor")
        void listarTotalMayor_Exito() throws Exception {
            // ARRANGE
            when(ventaService.listarMayorTotal(new BigDecimal("10.00"))).thenReturn(List.of(ventaResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas/total-mayor")
                            .param("total", "10.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(ventaService, times(1)).listarMayorTotal(new BigDecimal("10.00"));
        }

        @Test
        @DisplayName("GET /api/v1/ventas/buscar-por-fechas: Debe filtrar por rango de fechas ISO DATE_TIME")
        void listarEntreFechas_Exito() throws Exception {
            // ARRANGE
            LocalDateTime inicio = LocalDateTime.of(2026, 10, 15, 0, 0, 0);
            LocalDateTime fin = LocalDateTime.of(2026, 10, 15, 23, 59, 59);

            when(ventaService.listarEntreFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(ventaResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/ventas/buscar-por-fechas")
                            .param("fechaInicio", "2026-10-15T00:00:00")
                            .param("fechaFin", "2026-10-15T23:59:59"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(50)));

            verify(ventaService, times(1)).listarEntreFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }
}
package com.example.demo.controllers;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.services.ProductoService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@DisplayName("Pruebas de Integración Web para ProductoController")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    private ProductoRequestDTO productoRequestDTO;
    private ProductoResponseDTO productoResponseDTO;

    @BeforeEach
    void setUp() {
        productoRequestDTO = ProductoRequestDTO.builder()
                .codigoBarras("7751234567890")
                .nombre("Arroz Extra 1kg")
                .unidadMedida("Bolsa")
                .precio(new BigDecimal("4.50"))
                .stock(100)
                .activo(true)
                .build();

        productoResponseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .codigoBarras("7751234567890")
                .nombre("Arroz Extra 1kg")
                .unidadMedida("Bolsa")
                .precio(new BigDecimal("4.50"))
                .stock(100)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/productos - Registrar Producto")
    class RegistrarProductoTests {

        @Test
        @DisplayName("Debe retornar 201 Created cuando la solicitud es válida")
        void registrarProducto_Exito() throws Exception {
            // ARRANGE
            when(productoService.registrarProducto(any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.codigoBarras", is("7751234567890")))
                    .andExpect(jsonPath("$.nombre", is("Arroz Extra 1kg")))
                    .andExpect(jsonPath("$.precio", is(4.50)))
                    .andExpect(jsonPath("$.stock", is(100)));

            verify(productoService, times(1)).registrarProducto(any(ProductoRequestDTO.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción de validación cuando los datos son inválidos")
        void registrarProducto_SolicitudInvalida_Retorna400() throws Exception {
            // ARRANGE
            ProductoRequestDTO dtoInvalido = ProductoRequestDTO.builder()
                    .codigoBarras("")
                    .nombre("")
                    .precio(null)
                    .stock(null)
                    .build();

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andExpect(result -> assertInstanceOf(
                            org.springframework.web.bind.MethodArgumentNotValidException.class,
                            result.getResolvedException()
                    ));

            verify(productoService, never()).registrarProducto(any(ProductoRequestDTO.class));
        }

        @Test
        @DisplayName("Debe capturar conflicto cuando el código de barras ya existe")
        void registrarProducto_CodigoDuplicado_LanzaExcepcion() throws Exception {
            // ARRANGE
            when(productoService.registrarProducto(any(ProductoRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Ya existe un producto con el código de barras especificado"));

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoRequestDTO)))
                    .andExpect(result -> assertInstanceOf(DuplicateResourceException.class, result.getResolvedException()));

            verify(productoService, times(1)).registrarProducto(any(ProductoRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/productos - Consultas y Filtros de Productos")
    class ConsultarProductoTests {

        @Test
        @DisplayName("GET /api/v1/productos: Debe retornar 200 OK y la lista de activos")
        void listarProductos_Exito() throws Exception {
            // ARRANGE
            when(productoService.listarProductos()).thenReturn(List.of(productoResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].codigoBarras", is("7751234567890")));

            verify(productoService, times(1)).listarProductos();
        }

        @Test
        @DisplayName("GET /api/v1/productos/inactivos: Debe retornar 200 OK y la lista de inactivos")
        void listarProductosInactivos_Exito() throws Exception {
            // ARRANGE
            ProductoResponseDTO inactivoDTO = ProductoResponseDTO.builder()
                    .id(2L)
                    .codigoBarras("7759999999999")
                    .nombre("Aceite Vegetal")
                    .activo(false)
                    .build();

            when(productoService.listarProductosInactivos()).thenReturn(List.of(inactivoDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/inactivos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].activo", is(false)));

            verify(productoService, times(1)).listarProductosInactivos();
        }

        @Test
        @DisplayName("GET /api/v1/productos/{id}: Debe retornar 200 OK al buscar por ID")
        void buscarProductoPorId_Exito() throws Exception {
            // ARRANGE
            when(productoService.buscarPorIdDTO(1L)).thenReturn(productoResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("Arroz Extra 1kg")));

            verify(productoService, times(1)).buscarPorIdDTO(1L);
        }

        @Test
        @DisplayName("GET /api/v1/productos/buscar?codigoBarras={codigo}: Debe retornar 200 OK")
        void buscarPorCodigoBarras_Exito() throws Exception {
            // ARRANGE
            when(productoService.buscarPorCodigoBarras("7751234567890")).thenReturn(productoResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/buscar")
                            .param("codigoBarras", "7751234567890"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.codigoBarras", is("7751234567890")));

            verify(productoService, times(1)).buscarPorCodigoBarras("7751234567890");
        }

        @Test
        @DisplayName("GET /api/v1/productos/stock-mayor?stock={val}: Debe filtrar productos con mayor stock")
        void buscarPorStockMayor_Exito() throws Exception {
            // ARRANGE
            when(productoService.listarMayorStock(50)).thenReturn(List.of(productoResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/stock-mayor")
                            .param("stock", "50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].stock", is(100)));

            verify(productoService, times(1)).listarMayorStock(50);
        }

        @Test
        @DisplayName("GET /api/v1/productos/stock-menor?stock={val}: Debe filtrar productos con stock menor o igual")
        void buscarPorStockMenor_Exito() throws Exception {
            // ARRANGE
            when(productoService.listarMenorIgualStock(150)).thenReturn(List.of(productoResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/stock-menor")
                            .param("stock", "150"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(productoService, times(1)).listarMenorIgualStock(150);
        }

        @Test
        @DisplayName("GET /api/v1/productos/buscar-nombre?nombre={nombre}: Debe filtrar por coincidencia de nombre")
        void buscarPorNombre_Exito() throws Exception {
            // ARRANGE
            when(productoService.listarPorNombre("Arroz")).thenReturn(List.of(productoResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/productos/buscar-nombre")
                            .param("nombre", "Arroz"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre", is("Arroz Extra 1kg")));

            verify(productoService, times(1)).listarPorNombre("Arroz");
        }
    }

    @Nested
    @DisplayName("PUT, DELETE y PATCH /api/v1/productos - Modificaciones")
    class ModificarProductoTests {

        @Test
        @DisplayName("PUT /api/v1/productos/{id}: Debe actualizar y retornar 200 OK")
        void actualizarProducto_Exito() throws Exception {
            // ARRANGE
            when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(put("/api/v1/productos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productoRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));

            verify(productoService, times(1)).actualizarProducto(eq(1L), any(ProductoRequestDTO.class));
        }

        @Test
        @DisplayName("DELETE /api/v1/productos/{id}: Debe desactivar el producto y retornar 204 No Content")
        void desactivarProducto_Exito() throws Exception {
            // ARRANGE
            doNothing().when(productoService).desactivarProducto(1L);

            // ACT & ASSERT
            mockMvc.perform(delete("/api/v1/productos/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(productoService, times(1)).desactivarProducto(1L);
        }

        @Test
        @DisplayName("PATCH /api/v1/productos/{id}/activar: Debe reactivar el producto y retornar 204 No Content")
        void reactivarProducto_Exito() throws Exception {
            // ARRANGE
            doNothing().when(productoService).reactivarProducto(1L);

            // ACT & ASSERT
            mockMvc.perform(patch("/api/v1/productos/{id}/activar", 1L))
                    .andExpect(status().isNoContent());

            verify(productoService, times(1)).reactivarProducto(1L);
        }
    }
}
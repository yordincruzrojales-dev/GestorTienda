package com.example.demo.integration;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.models.Producto;
import com.example.demo.repositories.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Pruebas Integradoras de Productos (E2E)")
class ProductoIntegrationTest {

    private static final String BASE_URL = "/api/v1/productos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST " + BASE_URL + " - Registrar Producto")
    class RegistrarProductoTests {

        @Test
        @DisplayName("Debe registrar un producto exitosamente y retornar HTTP 201 Created")
        void registrarProducto_Exito_Retorna201() throws Exception {
            ProductoRequestDTO request = ProductoRequestDTO.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Aceite Primor 1L")
                    .unidadMedida("Botella")
                    .precio(new BigDecimal("11.50"))
                    .stock(50)
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.codigoBarras").value("7751234567890"))
                    .andExpect(jsonPath("$.nombre").value("Aceite Primor 1L"))
                    .andExpect(jsonPath("$.activo").value(true));

            assertThat(productoRepository.count()).isEqualTo(1);
            assertThat(productoRepository.existsByCodigoBarras("7751234567890")).isTrue();
        }

        @Test
        @DisplayName("Debe retornar HTTP 409 Conflict si el código de barras ya existe")
        void registrarProducto_CodigoBarrasDuplicado_Retorna409() throws Exception {
            productoRepository.save(Producto.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Existente")
                    .unidadMedida("Botella")
                    .precio(new BigDecimal("10.00"))
                    .stock(10)
                    .activo(true)
                    .build());

            ProductoRequestDTO requestDuplicado = ProductoRequestDTO.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Otro Producto")
                    .unidadMedida("Unidad")
                    .precio(new BigDecimal("5.00"))
                    .stock(20)
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDuplicado)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Debe retornar HTTP 400 Bad Request si los datos no cumplen con Bean Validation")
        void registrarProducto_DatosInvalidos_Retorna400() throws Exception {
            ProductoRequestDTO requestInvalido = ProductoRequestDTO.builder()
                    .codigoBarras("123") // Menor a 13 dígitos
                    .nombre("") // Vacío
                    .unidadMedida("U") // Menor a 2 caracteres
                    .precio(new BigDecimal("-1.00")) // Precio negativo
                    .stock(-5) // Stock negativo
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET " + BASE_URL + " - Consultas de Productos")
    class ConsultarProductosTests {

        @Test
        @DisplayName("Debe listar productos activos e inactivos por sus respectivos endpoints")
        void listarProductos_ActivosYDeinactivos_Retorna200() throws Exception {
            productoRepository.save(Producto.builder().codigoBarras("7750000000001").nombre("Arroz").unidadMedida("Bolsa").precio(new BigDecimal("4.00")).stock(100).activo(true).build());
            productoRepository.save(Producto.builder().codigoBarras("7750000000002").nombre("Fideo").unidadMedida("Bolsa").precio(new BigDecimal("2.50")).stock(50).activo(false).build());

            // Listar Activos
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].codigoBarras").value("7750000000001"));

            // Listar Inactivos
            mockMvc.perform(get(BASE_URL + "/inactivos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].codigoBarras").value("7750000000002"));
        }

        @Test
        @DisplayName("Debe buscar producto por código de barras exitosamente")
        void buscarPorCodigoBarras_Exito_Retorna200() throws Exception {
            productoRepository.save(Producto.builder()
                    .codigoBarras("7759999999999")
                    .nombre("Detergente Ariel")
                    .unidadMedida("Bolsa")
                    .precio(new BigDecimal("8.50"))
                    .stock(20)
                    .activo(true)
                    .build());

            mockMvc.perform(get(BASE_URL + "/buscar")
                            .param("codigoBarras", "7759999999999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Detergente Ariel"));
        }

        @Test
        @DisplayName("Debe buscar productos por coincidencias en el nombre (IgnoreCase)")
        void buscarPorNombre_Exito_Retorna200() throws Exception {
            productoRepository.save(Producto.builder().codigoBarras("7751111111111").nombre("Atún Real Sólido").unidadMedida("Lata").precio(new BigDecimal("6.00")).stock(30).activo(true).build());

            mockMvc.perform(get(BASE_URL + "/buscar-nombre")
                            .param("nombre", "atún"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Atún Real Sólido"));
        }

        @Test
        @DisplayName("Debe filtrar por umbrales de stock (mayor / menor-igual)")
        void buscarPorStock_Filtros_Retorna200() throws Exception {
            productoRepository.save(Producto.builder().codigoBarras("7751111111111").nombre("Stock Alto").unidadMedida("Caja").precio(new BigDecimal("10.00")).stock(50).activo(true).build());
            productoRepository.save(Producto.builder().codigoBarras("7752222222222").nombre("Stock Bajo").unidadMedida("Caja").precio(new BigDecimal("10.00")).stock(5).activo(true).build());

            // Mayor a 10
            mockMvc.perform(get(BASE_URL + "/stock-mayor").param("stock", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Stock Alto"));

            // Menor o igual a 10
            mockMvc.perform(get(BASE_URL + "/stock-menor").param("stock", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Stock Bajo"));
        }
    }

    @Nested
    @DisplayName("PUT " + BASE_URL + "/{id} - Actualizar Producto")
    class ActualizarProductoTests {

        @Test
        @DisplayName("Debe actualizar un producto y retornar HTTP 200 OK")
        void actualizarProducto_Exito_Retorna200() throws Exception {
            Producto guardado = productoRepository.save(Producto.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Nombre Antiguo")
                    .unidadMedida("Unidad")
                    .precio(new BigDecimal("5.00"))
                    .stock(10)
                    .activo(true)
                    .build());

            ProductoRequestDTO updateRequest = ProductoRequestDTO.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Nombre Nuevo")
                    .unidadMedida("Unidad")
                    .precio(new BigDecimal("7.50"))
                    .stock(15)
                    .activo(true)
                    .build();

            mockMvc.perform(put(BASE_URL + "/{id}", guardado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Nombre Nuevo"))
                    .andExpect(jsonPath("$.precio").value(7.50));

            Producto actualizadoBD = productoRepository.findById(guardado.getId()).orElseThrow();
            assertThat(actualizadoBD.getNombre()).isEqualTo("Nombre Nuevo");
        }
    }

    @Nested
    @DisplayName("DELETE & PATCH " + BASE_URL + "/{id} - Estado del Producto")
    class EstadoProductoTests {

        @Test
        @DisplayName("Debe desactivar un producto y retornar HTTP 204 No Content")
        void desactivarProducto_Exito_Retorna204() throws Exception {
            Producto guardado = productoRepository.save(Producto.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Para Desactivar")
                    .unidadMedida("Unidad")
                    .precio(new BigDecimal("5.00"))
                    .stock(10)
                    .activo(true)
                    .build());

            mockMvc.perform(delete(BASE_URL + "/{id}", guardado.getId()))
                    .andExpect(status().isNoContent());

            Producto deshabilitadoBD = productoRepository.findById(guardado.getId()).orElseThrow();
            assertThat(deshabilitadoBD.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Debe reactivar un producto inactivo y retornar HTTP 204 No Content")
        void reactivarProducto_Exito_Retorna204() throws Exception {
            Producto inactivo = productoRepository.save(Producto.builder()
                    .codigoBarras("7751234567890")
                    .nombre("Producto Inactivo")
                    .unidadMedida("Unidad")
                    .precio(new BigDecimal("5.00"))
                    .stock(10)
                    .activo(false)
                    .build());

            mockMvc.perform(patch(BASE_URL + "/{id}/activar", inactivo.getId()))
                    .andExpect(status().isNoContent());

            Producto reactivadoBD = productoRepository.findById(inactivo.getId()).orElseThrow();
            assertThat(reactivadoBD.getActivo()).isTrue();
        }
    }
}
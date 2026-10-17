package com.example.demo.services;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ProductoMapper;
import com.example.demo.models.Producto;
import com.example.demo.repositories.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ProductoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoActivo;
    private Producto productoInactivo;
    private ProductoRequestDTO productoRequestDTO;
    private ProductoResponseDTO productoResponseDTO;

    @BeforeEach
    void setUp() {
        productoRequestDTO = ProductoRequestDTO.builder()
                .codigoBarras("7791234567890")
                .nombre("Leche Entera 1L")
                .unidadMedida("UNIDAD")
                .precio(new BigDecimal("4.50"))
                .stock(50)
                .activo(true)
                .build();

        productoActivo = Producto.builder()
                .id(1L)
                .codigoBarras("7791234567890")
                .nombre("Leche Entera 1L")
                .unidadMedida("UNIDAD")
                .precio(new BigDecimal("4.50"))
                .stock(50)
                .activo(true)
                .build();

        productoInactivo = Producto.builder()
                .id(2L)
                .codigoBarras("7790000000000")
                .nombre("Galletas de Chocolate")
                .unidadMedida("UNIDAD")
                .precio(new BigDecimal("2.00"))
                .stock(0)
                .activo(false)
                .build();

        productoResponseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .codigoBarras("7791234567890")
                .nombre("Leche Entera 1L")
                .unidadMedida("UNIDAD")
                .precio(new BigDecimal("4.50"))
                .stock(50)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para registrarProducto")
    class RegistrarProductoTests {

        @Test
        @DisplayName("Debe registrar un producto exitosamente cuando el código de barras no existe")
        void registrarProducto_Exito() {
            // ARRANGE
            when(productoRepository.existsByCodigoBarras("7791234567890")).thenReturn(false);
            when(productoMapper.toEntity(productoRequestDTO)).thenReturn(productoActivo);
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            ProductoResponseDTO resultado = productoService.registrarProducto(productoRequestDTO);

            // ASSERT
            assertNotNull(resultado);
            assertEquals("7791234567890", resultado.getCodigoBarras());
            assertEquals("Leche Entera 1L", resultado.getNombre());

            verify(productoRepository, times(1)).existsByCodigoBarras("7791234567890");
            verify(productoRepository, times(1)).save(any(Producto.class));
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException cuando el código de barras ya existe")
        void registrarProducto_CodigoBarrasDuplicado_LanzaExcepcion() {
            // ARRANGE
            when(productoRepository.existsByCodigoBarras("7791234567890")).thenReturn(true);

            // ACT & ASSERT
            assertThrows(DuplicateResourceException.class, () -> {
                productoService.registrarProducto(productoRequestDTO);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para búsquedas y consultas de Productos")
    class BuscarProductoTests {

        @Test
        @DisplayName("buscarPorIdDTO: Debe retornar DTO cuando el ID existe y está activo")
        void buscarPorIdDTO_Exito() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            ProductoResponseDTO resultado = productoService.buscarPorIdDTO(1L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("buscarPorId: Debe retornar la entidad cuando el ID existe")
        void buscarPorId_Entidad_Exito() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));

            // ACT
            Producto resultado = productoService.buscarPorId(1L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals("Leche Entera 1L", resultado.getNombre());
        }

        @Test
        @DisplayName("buscarPorId: Debe lanzar ResourceNotFoundException cuando no existe")
        void buscarPorId_NoEncontrado_LanzaExcepcion() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                productoService.buscarPorId(99L);
            });
        }

        @Test
        @DisplayName("buscarPorCodigoBarras: Debe retornar DTO cuando el código existe y está activo")
        void buscarPorCodigoBarras_Exito() {
            // ARRANGE
            when(productoRepository.findByCodigoBarrasAndActivoTrue("7791234567890")).thenReturn(Optional.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            ProductoResponseDTO resultado = productoService.buscarPorCodigoBarras("7791234567890");

            // ASSERT
            assertNotNull(resultado);
            assertEquals("7791234567890", resultado.getCodigoBarras());
        }

        @Test
        @DisplayName("buscarPorCodigoBarras: Debe lanzar ResourceNotFoundException cuando el código no existe")
        void buscarPorCodigoBarras_NoEncontrado_LanzaExcepcion() {
            // ARRANGE
            when(productoRepository.findByCodigoBarrasAndActivoTrue("0000000000000")).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                productoService.buscarPorCodigoBarras("0000000000000");
            });
        }

        @Test
        @DisplayName("obtenerProductosPorIdsMap: Debe retornar Map<Long, Producto> con IDs válidos")
        void obtenerProductosPorIdsMap_Exito() {
            // ARRANGE
            List<Long> idsInput = List.of(1L, 1L); // Contiene duplicados para probar distinct()
            when(productoRepository.findByIdInAndActivoTrue(List.of(1L))).thenReturn(List.of(productoActivo));

            // ACT
            Map<Long, Producto> mapaResultado = productoService.obtenerProductosPorIdsMap(idsInput);

            // ASSERT
            assertNotNull(mapaResultado);
            assertEquals(1, mapaResultado.size());
            assertTrue(mapaResultado.containsKey(1L));
            assertEquals("Leche Entera 1L", mapaResultado.get(1L).getNombre());
        }

        @Test
        @DisplayName("obtenerProductosPorIdsMap: Debe lanzar ResourceNotFoundException si falta algún producto")
        void obtenerProductosPorIdsMap_FaltaUnProducto_LanzaExcepcion() {
            // ARRANGE
            List<Long> idsInput = List.of(1L, 2L);
            when(productoRepository.findByIdInAndActivoTrue(idsInput)).thenReturn(List.of(productoActivo)); // Solo encuentra 1 de los 2

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                productoService.obtenerProductosPorIdsMap(idsInput);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas para listados de Productos")
    class ListarProductosTests {

        @Test
        @DisplayName("listarProductos: Debe retornar productos activos")
        void listarProductos_Exito() {
            // ARRANGE
            when(productoRepository.findByActivoTrue()).thenReturn(List.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            List<ProductoResponseDTO> resultado = productoService.listarProductos();

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("listarProductosInactivos: Debe retornar productos inactivos")
        void listarProductosInactivos_Exito() {
            // ARRANGE
            when(productoRepository.findByActivoFalse()).thenReturn(List.of(productoInactivo));
            when(productoMapper.toResponseDTO(productoInactivo)).thenReturn(productoResponseDTO);

            // ACT
            List<ProductoResponseDTO> resultado = productoService.listarProductosInactivos();

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("listarMenorIgualStock: Debe retornar productos con stock menor o igual al filtro")
        void listarMenorIgualStock_Exito() {
            // ARRANGE
            when(productoRepository.findByStockLessThanEqualAndActivoTrue(10)).thenReturn(List.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            List<ProductoResponseDTO> resultado = productoService.listarMenorIgualStock(10);

            // ASSERT
            assertNotNull(resultado);
            verify(productoRepository, times(1)).findByStockLessThanEqualAndActivoTrue(10);
        }

        @Test
        @DisplayName("listarMayorStock: Debe retornar productos con stock mayor al filtro")
        void listarMayorStock_Exito() {
            // ARRANGE
            when(productoRepository.findByStockGreaterThanAndActivoTrue(5)).thenReturn(List.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            List<ProductoResponseDTO> resultado = productoService.listarMayorStock(5);

            // ASSERT
            assertNotNull(resultado);
            verify(productoRepository, times(1)).findByStockGreaterThanAndActivoTrue(5);
        }

        @Test
        @DisplayName("listarPorNombre: Debe retornar productos que coincidan con la búsqueda")
        void listarPorNombre_Exito() {
            // ARRANGE
            when(productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("leche")).thenReturn(List.of(productoActivo));
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            List<ProductoResponseDTO> resultado = productoService.listarPorNombre("leche");

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(productoRepository, times(1)).findByNombreContainingIgnoreCaseAndActivoTrue("leche");
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizarProducto")
    class ActualizarProductoTests {

        @Test
        @DisplayName("Debe actualizar manteniendo el mismo código de barras")
        void actualizarProducto_MismoCodigo_Exito() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            ProductoResponseDTO resultado = productoService.actualizarProducto(1L, productoRequestDTO);

            // ASSERT
            assertNotNull(resultado);
            verify(productoRepository, never()).existsByCodigoBarras(anyString());
            verify(productoRepository, times(1)).save(any(Producto.class));
        }

        @Test
        @DisplayName("Debe actualizar con un nuevo código de barras no existente")
        void actualizarProducto_NuevoCodigoValido_Exito() {
            // ARRANGE
            ProductoRequestDTO dtoNuevoCodigo = ProductoRequestDTO.builder()
                    .codigoBarras("9999999999999")
                    .nombre("Leche Deslactosada 1L")
                    .build();

            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));
            when(productoRepository.existsByCodigoBarras("9999999999999")).thenReturn(false);
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);
            when(productoMapper.toResponseDTO(productoActivo)).thenReturn(productoResponseDTO);

            // ACT
            ProductoResponseDTO resultado = productoService.actualizarProducto(1L, dtoNuevoCodigo);

            // ASSERT
            assertNotNull(resultado);
            verify(productoRepository, times(1)).existsByCodigoBarras("9999999999999");
            verify(productoRepository, times(1)).save(any(Producto.class));
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException cuando el nuevo código de barras pertenece a otro producto")
        void actualizarProducto_NuevoCodigoDuplicado_LanzaExcepcion() {
            // ARRANGE
            ProductoRequestDTO dtoNuevoCodigo = ProductoRequestDTO.builder()
                    .codigoBarras("9999999999999")
                    .build();

            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));
            when(productoRepository.existsByCodigoBarras("9999999999999")).thenReturn(true);

            // ACT & ASSERT
            assertThrows(DuplicateResourceException.class, () -> {
                productoService.actualizarProducto(1L, dtoNuevoCodigo);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para cambiar estado (desactivar/reactivar)")
    class EstadoProductoTests {

        @Test
        @DisplayName("desactivarProducto: Debe desactivar el producto si existe y está activo")
        void desactivarProducto_Exito() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(productoActivo));

            // ACT
            productoService.desactivarProducto(1L);

            // ASSERT
            assertFalse(productoActivo.getActivo());
            verify(productoRepository, times(1)).save(productoActivo);
        }

        @Test
        @DisplayName("desactivarProducto: Debe lanzar ResourceNotFoundException si no existe o ya está inactivo")
        void desactivarProducto_NoExiste_LanzaExcepcion() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                productoService.desactivarProducto(99L);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("reactivarProducto: Debe reactivar el producto cuando existe y está inactivo")
        void reactivarProducto_Exito() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoFalse(2L)).thenReturn(Optional.of(productoInactivo));

            // ACT
            productoService.reactivarProducto(2L);

            // ASSERT
            assertTrue(productoInactivo.getActivo());
            verify(productoRepository, times(1)).save(productoInactivo);
        }

        @Test
        @DisplayName("reactivarProducto: Debe lanzar ResourceNotFoundException si ya está activo o no existe")
        void reactivarProducto_NoExiste_LanzaExcepcion() {
            // ARRANGE
            when(productoRepository.findByIdAndActivoFalse(1L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                productoService.reactivarProducto(1L);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }
    }
}
package com.example.demo.services;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.dtos.cliente.ClienteResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ClienteMapper;
import com.example.demo.models.Cliente;
import com.example.demo.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteActivo;
    private Cliente clienteInactivo;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = ClienteRequestDTO.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .telefono("987654321")
                .activo(true)
                .build();

        clienteActivo = Cliente.builder()
                .id(1L)
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .telefono("987654321")
                .activo(true)
                .build();

        clienteInactivo = Cliente.builder()
                .id(2L)
                .dni("87654321")
                .nombres("Maria")
                .apellidos("Gómez")
                .telefono("912345678")
                .activo(false)
                .build();

        clienteResponseDTO = ClienteResponseDTO.builder()
                .id(1L)
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .telefono("987654321")
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para registrarCliente")
    class RegistrarClienteTests {

        @Test
        @DisplayName("Debe registrar cliente exitosamente cuando el DNI no existe")
        void registrarCliente_Exito() {
            // ARRANGE
            when(clienteRepository.existsByDni("12345678")).thenReturn(false);
            when(clienteMapper.toEntity(clienteRequestDTO)).thenReturn(clienteActivo);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActivo);
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            ClienteResponseDTO resultado = clienteService.registrarCliente(clienteRequestDTO);

            // ASSERT
            assertNotNull(resultado);
            assertEquals("12345678", resultado.getDni());
            assertEquals("Juan", resultado.getNombres());

            verify(clienteRepository, times(1)).existsByDni("12345678");
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException cuando el DNI ya está registrado")
        void registrarCliente_DniDuplicado_LanzaExcepcion() {
            // ARRANGE
            when(clienteRepository.existsByDni("12345678")).thenReturn(true);

            // ACT & ASSERT
            assertThrows(DuplicateResourceException.class, () -> {
                clienteService.registrarCliente(clienteRequestDTO);
            });

            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para búsquedas de Cliente")
    class BuscarClienteTests {

        @Test
        @DisplayName("buscarPorDni: Debe retornar cliente cuando existe y está activo")
        void buscarPorDni_Exito() {
            // ARRANGE
            when(clienteRepository.findByDniAndActivoTrue("12345678")).thenReturn(Optional.of(clienteActivo));
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            ClienteResponseDTO resultado = clienteService.buscarPorDni("12345678");

            // ASSERT
            assertNotNull(resultado);
            assertEquals("12345678", resultado.getDni());
            verify(clienteRepository, times(1)).findByDniAndActivoTrue("12345678");
        }

        @Test
        @DisplayName("buscarPorDni: Debe lanzar ResourceNotFoundException cuando no existe o está inactivo")
        void buscarPorDni_NoEncontrado_LanzaExcepcion() {
            // ARRANGE
            when(clienteRepository.findByDniAndActivoTrue("99999999")).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                clienteService.buscarPorDni("99999999");
            });
        }

        @Test
        @DisplayName("buscarPorIdDTO: Debe retornar cliente DTO cuando el ID existe y está activo")
        void buscarPorIdDTO_Exito() {
            // ARRANGE
            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            ClienteResponseDTO resultado = clienteService.buscarPorIdDTO(1L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("buscarPorId: Debe retornar la entidad Cliente cuando el ID existe")
        void buscarPorId_Entidad_Exito() {
            // ARRANGE
            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));

            // ACT
            Cliente resultado = clienteService.buscarPorId(1L);

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Juan", resultado.getNombres());
        }

        @Test
        @DisplayName("listarClientes: Debe retornar la lista de clientes activos")
        void listarClientes_Exito() {
            // ARRANGE
            when(clienteRepository.findByActivoTrue()).thenReturn(List.of(clienteActivo));
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            List<ClienteResponseDTO> resultado = clienteService.listarClientes();

            // ASSERT
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(clienteRepository, times(1)).findByActivoTrue();
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizarCliente")
    class ActualizarClienteTests {

        @Test
        @DisplayName("Debe actualizar cliente correctamente manteniendo el mismo DNI")
        void actualizarCliente_MismoDni_Exito() {
            // ARRANGE
            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActivo);
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            ClienteResponseDTO resultado = clienteService.actualizarCliente(1L, clienteRequestDTO);

            // ASSERT
            assertNotNull(resultado);
            verify(clienteRepository, never()).existsByDni(anyString()); // No consulta duplicado si el DNI es idéntico
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe actualizar cliente cambiando a un nuevo DNI no existente")
        void actualizarCliente_NuevoDniValido_Exito() {
            // ARRANGE
            ClienteRequestDTO dtoNuevoDni = ClienteRequestDTO.builder()
                    .dni("87654321")
                    .nombres("Juan Carlos")
                    .build();

            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.existsByDni("87654321")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActivo);
            when(clienteMapper.toResponseDTO(clienteActivo)).thenReturn(clienteResponseDTO);

            // ACT
            ClienteResponseDTO resultado = clienteService.actualizarCliente(1L, dtoNuevoDni);

            // ASSERT
            assertNotNull(resultado);
            verify(clienteRepository, times(1)).existsByDni("87654321");
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException al cambiar a un DNI ya usado por otro cliente")
        void actualizarCliente_NuevoDniDuplicado_LanzaExcepcion() {
            // ARRANGE
            ClienteRequestDTO dtoNuevoDni = ClienteRequestDTO.builder()
                    .dni("87654321")
                    .build();

            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.existsByDni("87654321")).thenReturn(true);

            // ACT & ASSERT
            assertThrows(DuplicateResourceException.class, () -> {
                clienteService.actualizarCliente(1L, dtoNuevoDni);
            });

            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para cambiar estado (desactivar/reactivar)")
    class EstadoClienteTests {

        @Test
        @DisplayName("desactivarCliente: Debe desactivar el cliente si existe y está activo")
        void desactivarCliente_Exito() {
            // ARRANGE
            when(clienteRepository.existsByIdAndActivoTrue(1L)).thenReturn(true);
            when(clienteRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(clienteActivo));

            // ACT
            clienteService.desactivarCliente(1L);

            // ASSERT
            assertFalse(clienteActivo.getActivo()); // Verifica que cambio su estado interno
            verify(clienteRepository, times(1)).save(clienteActivo);
        }

        @Test
        @DisplayName("desactivarCliente: Debe lanzar ResourceNotFoundException si no existe o ya está inactivo")
        void desactivarCliente_NoExiste_LanzaExcepcion() {
            // ARRANGE
            when(clienteRepository.existsByIdAndActivoTrue(99L)).thenReturn(false);

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                clienteService.desactivarCliente(99L);
            });

            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("reactivarCliente: Debe reactivar cliente cuando existe y está inactivo")
        void reactivarCliente_Exito() {
            // ARRANGE
            when(clienteRepository.findByIdAndActivoFalse(2L)).thenReturn(Optional.of(clienteInactivo));

            // ACT
            clienteService.reactivarCliente(2L);

            // ASSERT
            assertTrue(clienteInactivo.getActivo());
            verify(clienteRepository, times(1)).save(clienteInactivo);
        }

        @Test
        @DisplayName("reactivarCliente: Debe lanzar ResourceNotFoundException si ya está activo o no existe")
        void reactivarCliente_NoExisteOInactivo_LanzaExcepcion() {
            // ARRANGE
            when(clienteRepository.findByIdAndActivoFalse(1L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> {
                clienteService.reactivarCliente(1L);
            });

            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }
}
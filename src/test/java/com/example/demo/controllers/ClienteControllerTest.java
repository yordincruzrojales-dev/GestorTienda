package com.example.demo.controllers;

import java.util.List;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.dtos.cliente.ClienteResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@DisplayName("Pruebas de Integración Web para ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

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
    @DisplayName("POST /api/v1/clientes - Registrar Cliente")
    class RegistrarClienteTests {

        @Test
        @DisplayName("Debe retornar 201 Created cuando la solicitud es válida")
        void registrarCliente_Exito() throws Exception {
            // ARRANGE
            when(clienteService.registrarCliente(any(ClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.dni", is("12345678")))
                    .andExpect(jsonPath("$.nombres", is("Juan")));

            verify(clienteService, times(1)).registrarCliente(any(ClienteRequestDTO.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción de validación cuando los datos son inválidos")
        void registrarCliente_DniInvalido_Retorna400() throws Exception {
            // ARRANGE
            ClienteRequestDTO dtoInvalido = ClienteRequestDTO.builder()
                    .dni("123")
                    .nombres("")
                    .apellidos("")
                    .telefono("")
                    .activo(null)
                    .build();

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andExpect(result -> assertInstanceOf(
                            org.springframework.web.bind.MethodArgumentNotValidException.class,
                            result.getResolvedException()
                    ));

            verify(clienteService, never()).registrarCliente(any(ClienteRequestDTO.class));
        }

        @Test
        @DisplayName("Debe retornar conflicto cuando el DNI ya está registrado")
        void registrarCliente_DniDuplicado_LanzaExcepcion() throws Exception {
            // ARRANGE
            when(clienteService.registrarCliente(any(ClienteRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Ya existe un cliente con ese DNI"));

            // ACT & ASSERT
            mockMvc.perform(post("/api/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(result -> assertInstanceOf(DuplicateResourceException.class, result.getResolvedException()));

            verify(clienteService, times(1)).registrarCliente(any(ClienteRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/clientes - Consultas de Clientes")
    class ConsultarClienteTests {

        @Test
        @DisplayName("GET /api/v1/clientes: Debe retornar 200 OK y la lista de clientes")
        void listarClientes_Exito() throws Exception {
            // ARRANGE
            when(clienteService.listarClientes()).thenReturn(List.of(clienteResponseDTO));

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].dni", is("12345678")));

            verify(clienteService, times(1)).listarClientes();
        }

        @Test
        @DisplayName("GET /api/v1/clientes/{id}: Debe retornar 200 OK al buscar por ID existente")
        void buscarClientePorId_Exito() throws Exception {
            // ARRANGE
            when(clienteService.buscarPorIdDTO(1L)).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/clientes/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.dni", is("12345678")));

            verify(clienteService, times(1)).buscarPorIdDTO(1L);
        }

        @Test
        @DisplayName("GET /api/v1/clientes/buscar?dni={dni}: Debe retornar 200 OK al buscar por @RequestParam DNI")
        void buscarPorDni_Exito() throws Exception {
            // ARRANGE
            when(clienteService.buscarPorDni("12345678")).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(get("/api/v1/clientes/buscar")
                            .param("dni", "12345678"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dni", is("12345678")));

            verify(clienteService, times(1)).buscarPorDni("12345678");
        }
    }

    @Nested
    @DisplayName("PUT, DELETE y PATCH /api/v1/clientes - Modificaciones")
    class ModificarClienteTests {

        @Test
        @DisplayName("PUT /api/v1/clientes/{id}: Debe actualizar y retornar 200 OK")
        void actualizarCliente_Exito() throws Exception {
            // ARRANGE
            when(clienteService.actualizarCliente(eq(1L), any(ClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(put("/api/v1/clientes/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));

            verify(clienteService, times(1)).actualizarCliente(eq(1L), any(ClienteRequestDTO.class));
        }

        @Test
        @DisplayName("DELETE /api/v1/clientes/{id}: Debe desactivar el cliente y retornar 204 No Content")
        void desactivarCliente_Exito() throws Exception {
            // ARRANGE
            doNothing().when(clienteService).desactivarCliente(1L);

            // ACT & ASSERT
            mockMvc.perform(delete("/api/v1/clientes/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(clienteService, times(1)).desactivarCliente(1L);
        }

        @Test
        @DisplayName("PATCH /api/v1/clientes/{id}/activar: Debe reactivar el cliente y retornar 204 No Content")
        void reactivarCliente_Exito() throws Exception {
            // ARRANGE
            doNothing().when(clienteService).reactivarCliente(1L);

            // ACT & ASSERT
            mockMvc.perform(patch("/api/v1/clientes/{id}/activar", 1L))
                    .andExpect(status().isNoContent());

            verify(clienteService, times(1)).reactivarCliente(1L);
        }
    }
}
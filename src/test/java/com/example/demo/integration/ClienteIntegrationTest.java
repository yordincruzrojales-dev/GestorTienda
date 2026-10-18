package com.example.demo.integration;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.models.Cliente;
import com.example.demo.repositories.ClienteRepository;
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
@DisplayName("Pruebas Integradoras de Clientes (E2E)")
class ClienteIntegrationTest {

    private static final String BASE_URL = "/api/v1/clientes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST " + BASE_URL + " - Registrar Cliente")
    class RegistrarClienteTests {

        @Test
        @DisplayName("Debe registrar cliente exitosamente y retornar HTTP 201 Created")
        void registrarCliente_Exito_Retorna201() throws Exception {
            ClienteRequestDTO request = ClienteRequestDTO.builder()
                    .dni("12345678")
                    .nombres("Juan Carlos")
                    .apellidos("Pérez Gómez")
                    .telefono("987654321")
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.dni").value("12345678"))
                    .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                    .andExpect(jsonPath("$.activo").value(true));

            // Verificación en BD real
            assertThat(clienteRepository.count()).isEqualTo(1);
            assertThat(clienteRepository.existsByDni("12345678")).isTrue();
        }

        @Test
        @DisplayName("Debe retornar HTTP 409 Conflict cuando el DNI ya está registrado")
        void registrarCliente_DniDuplicado_Retorna409() throws Exception {
            // Guardar previo en BD
            clienteRepository.save(Cliente.builder()
                    .dni("12345678")
                    .nombres("Existente")
                    .apellidos("Cliente")
                    .telefono("900000000")
                    .activo(true)
                    .build());

            ClienteRequestDTO requestDuplicado = ClienteRequestDTO.builder()
                    .dni("12345678")
                    .nombres("Otro")
                    .apellidos("Usuario")
                    .telefono("911111111")
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDuplicado)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Debe retornar HTTP 400 Bad Request si los campos no pasan las validaciones")
        void registrarCliente_DatosInvalidos_Retorna400() throws Exception {
            ClienteRequestDTO requestInvalido = ClienteRequestDTO.builder()
                    .dni("123") // DNI corto (debe tener 8)
                    .nombres("") // Vacío
                    .apellidos("Pérez")
                    .telefono("123") // Teléfono corto
                    .activo(true)
                    .build();

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET " + BASE_URL + " - Consultas de Clientes")
    class ConsultarClientesTests {

        @Test
        @DisplayName("Debe listar únicamente los clientes activos con HTTP 200 OK")
        void listarClientes_RetornaSoloActivos() throws Exception {
            clienteRepository.save(Cliente.builder().dni("11112222").nombres("Activo 1").apellidos("AA").telefono("911111111").activo(true).build());
            clienteRepository.save(Cliente.builder().dni("33334444").nombres("Inactivo").apellidos("BB").telefono("922222222").activo(false).build());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].dni").value("11112222"));
        }

        @Test
        @DisplayName("Debe buscar cliente por ID exitosamente con HTTP 200 OK")
        void buscarClientePorId_Exito_Retorna200() throws Exception {
            Cliente guardado = clienteRepository.save(Cliente.builder()
                    .dni("55556666")
                    .nombres("María")
                    .apellidos("López")
                    .telefono("933333333")
                    .activo(true)
                    .build());

            mockMvc.perform(get(BASE_URL + "/{id}", guardado.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(guardado.getId()))
                    .andExpect(jsonPath("$.dni").value("55556666"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 Not Found cuando el ID no existe o está inactivo")
        void buscarClientePorId_NoEncontrado_Retorna404() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", 999L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debe buscar cliente por DNI usando RequestParam con HTTP 200 OK")
        void buscarPorDni_Exito_Retorna200() throws Exception {
            clienteRepository.save(Cliente.builder()
                    .dni("77778888")
                    .nombres("Pedro")
                    .apellidos("Sánchez")
                    .telefono("944444444")
                    .activo(true)
                    .build());

            mockMvc.perform(get(BASE_URL + "/buscar")
                            .param("dni", "77778888"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombres").value("Pedro"));
        }
    }

    @Nested
    @DisplayName("PUT " + BASE_URL + "/{id} - Actualizar Cliente")
    class ActualizarClienteTests {

        @Test
        @DisplayName("Debe actualizar cliente existente y retornar HTTP 200 OK")
        void actualizarCliente_Exito_Retorna200() throws Exception {
            Cliente guardado = clienteRepository.save(Cliente.builder()
                    .dni("12345678")
                    .nombres("Original")
                    .apellidos("Nombre")
                    .telefono("900000000")
                    .activo(true)
                    .build());

            ClienteRequestDTO updateRequest = ClienteRequestDTO.builder()
                    .dni("12345678")
                    .nombres("Modificado")
                    .apellidos("Actualizado")
                    .telefono("999999999")
                    .activo(true)
                    .build();

            mockMvc.perform(put(BASE_URL + "/{id}", guardado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombres").value("Modificado"))
                    .andExpect(jsonPath("$.telefono").value("999999999"));

            Cliente clienteEnBD = clienteRepository.findById(guardado.getId()).orElseThrow();
            assertThat(clienteEnBD.getNombres()).isEqualTo("Modificado");
        }
    }

    @Nested
    @DisplayName("DELETE & PATCH " + BASE_URL + "/{id} - Estado del Cliente")
    class EstadoClienteTests {

        @Test
        @DisplayName("Debe desactivar cliente (borrado lógico) y retornar HTTP 204 No Content")
        void desactivarCliente_Exito_Retorna204() throws Exception {
            Cliente guardado = clienteRepository.save(Cliente.builder()
                    .dni("88889999")
                    .nombres("Para")
                    .apellidos("Desactivar")
                    .telefono("955555555")
                    .activo(true)
                    .build());

            mockMvc.perform(delete(BASE_URL + "/{id}", guardado.getId()))
                    .andExpect(status().isNoContent());

            Cliente clienteEnBD = clienteRepository.findById(guardado.getId()).orElseThrow();
            assertThat(clienteEnBD.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Debe reactivar un cliente desactivado y retornar HTTP 204 No Content")
        void reactivarCliente_Exito_Retorna204() throws Exception {
            Cliente inactivo = clienteRepository.save(Cliente.builder()
                    .dni("44445555")
                    .nombres("Cliente")
                    .apellidos("Inactivo")
                    .telefono("966666666")
                    .activo(false)
                    .build());

            mockMvc.perform(patch(BASE_URL + "/{id}/activar", inactivo.getId()))
                    .andExpect(status().isNoContent());

            Cliente clienteEnBD = clienteRepository.findById(inactivo.getId()).orElseThrow();
            assertThat(clienteEnBD.getActivo()).isTrue();
        }
    }
}